package com.splitease.managers;

import com.splitease.database.DatabaseHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UserManager {
    private DatabaseHelper dbHelper;
    private String currentUser;

    public UserManager() {
        this.dbHelper = DatabaseHelper.getInstance();
        this.currentUser = null;
    }

    public boolean register(String username, String password, String email) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            System.out.println("All fields are required!");
            return false;
        }

        String passwordHash = hashPassword(password);
        String query = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";

        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            
            System.out.println("Registration successful! Welcome, " + username + "!");
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Username already exists. Please choose a different username.");
            } else {
                System.err.println("Registration error: " + e.getMessage());
            }
            return false;
        }
    }

    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            System.out.println("Username and password are required!");
            return false;
        }

        String passwordHash = hashPassword(password);
        String query = "SELECT username FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    currentUser = rs.getString("username");
                    System.out.println("Login successful! Welcome back, " + currentUser + "!");
                    return true;
                } else {
                    System.out.println("Invalid username or password.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            return false;
        }
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("Goodbye, " + currentUser + "!");
            currentUser = null;
        }
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}