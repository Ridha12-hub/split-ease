package oop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManagement {

    public String registerUser(String username, String password) {
        if (username.isBlank() || password.isBlank()) {
            return "Username and password cannot be blank.";
        }

        String passwordHash = String.valueOf(password.hashCode());
        String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.executeUpdate();
            return "✅ User '" + username + "' registered successfully!";
        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                return "Error: Username '" + username + "' already exists.";
            }
            return "Database error: " + e.getMessage();
        }
    }

    public boolean loginUser(String username, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        String passwordHash = String.valueOf(password.hashCode());

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return storedHash.equals(passwordHash);
            }
            return false; // User not found
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }
}