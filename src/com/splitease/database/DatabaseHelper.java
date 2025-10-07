package com.splitease.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:./splitease.db";
    private static DatabaseHelper instance;

    private DatabaseHelper() {
        // Private constructor for singleton
    }

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void initializeDatabase() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                username TEXT PRIMARY KEY,
                password_hash TEXT NOT NULL,
                email TEXT NOT NULL
            )
        """;

        String createBillsTable = """
            CREATE TABLE IF NOT EXISTS bills (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                amount REAL NOT NULL,
                payer_username TEXT NOT NULL,
                date TEXT NOT NULL,
                description TEXT,
                FOREIGN KEY (payer_username) REFERENCES users(username)
            )
        """;

        String createBillUsersTable = """
            CREATE TABLE IF NOT EXISTS bill_users (
                bill_id INTEGER,
                username TEXT,
                PRIMARY KEY (bill_id, username),
                FOREIGN KEY (bill_id) REFERENCES bills(id),
                FOREIGN KEY (username) REFERENCES users(username)
            )
        """;

        String createBillSharesTable = """
            CREATE TABLE IF NOT EXISTS bill_shares (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                bill_id INTEGER NOT NULL,
                username TEXT NOT NULL,
                share_amount REAL NOT NULL,
                due_date TEXT NOT NULL,
                status TEXT DEFAULT 'Pending',
                FOREIGN KEY (bill_id) REFERENCES bills(id),
                FOREIGN KEY (username) REFERENCES users(username)
            )
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createUsersTable);
            stmt.execute(createBillsTable);
            stmt.execute(createBillUsersTable);
            stmt.execute(createBillSharesTable);
            
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        String query = "SELECT username FROM users";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching usernames: " + e.getMessage());
        }
        
        return usernames;
    }
}