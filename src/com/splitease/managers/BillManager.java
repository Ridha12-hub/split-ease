package com.splitease.managers;

import com.splitease.database.DatabaseHelper;
import com.splitease.models.Bill;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillManager {
    private DatabaseHelper dbHelper;

    public BillManager() {
        this.dbHelper = DatabaseHelper.getInstance();
    }

    public int createBill(double amount, String payerUsername, String description, List<String> participants) {
        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return -1;
        }

        if (participants == null || participants.isEmpty()) {
            System.out.println("At least one participant is required.");
            return -1;
        }

        String currentDate = LocalDate.now().toString();
        String insertBillQuery = "INSERT INTO bills (amount, payer_username, date, description) VALUES (?, ?, ?, ?)";
        String insertBillUserQuery = "INSERT INTO bill_users (bill_id, username) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            conn.setAutoCommit(false);

            int billId;
            try (PreparedStatement pstmt = conn.prepareStatement(insertBillQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setDouble(1, amount);
                pstmt.setString(2, payerUsername);
                pstmt.setString(3, currentDate);
                pstmt.setString(4, description);
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        billId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating bill failed, no ID obtained.");
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertBillUserQuery)) {
                for (String participant : participants) {
                    pstmt.setInt(1, billId);
                    pstmt.setString(2, participant);
                    pstmt.executeUpdate();
                }
            }

            conn.commit();
            System.out.println("Bill created successfully with ID: " + billId);
            return billId;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            System.err.println("Error creating bill: " + e.getMessage());
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String query = """
            SELECT b.id, b.amount, b.payer_username, b.date, b.description,
                   GROUP_CONCAT(bu.username, ', ') as participants
            FROM bills b
            LEFT JOIN bill_users bu ON b.id = bu.bill_id
            GROUP BY b.id
            ORDER BY b.date DESC
        """;

        try (Connection conn = dbHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Bill bill = new Bill(
                    rs.getInt("id"),
                    rs.getDouble("amount"),
                    rs.getString("payer_username"),
                    rs.getString("date"),
                    rs.getString("description"),
                    rs.getString("participants")
                );
                bills.add(bill);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bills: " + e.getMessage());
        }

        return bills;
    }

    public void displayAllBills() {
        List<Bill> bills = getAllBills();
        
        if (bills.isEmpty()) {
            System.out.println("No bills found.");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println("ALL BILLS");
        System.out.println("=".repeat(100));
        
        for (Bill bill : bills) {
            System.out.println(bill);
        }
        
        System.out.println("=".repeat(100) + "\n");
    }

    public Bill getBillById(int billId) {
        String query = """
            SELECT b.id, b.amount, b.payer_username, b.date, b.description,
                   GROUP_CONCAT(bu.username, ', ') as participants
            FROM bills b
            LEFT JOIN bill_users bu ON b.id = bu.bill_id
            WHERE b.id = ?
            GROUP BY b.id
        """;

        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, billId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Bill(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getString("payer_username"),
                        rs.getString("date"),
                        rs.getString("description"),
                        rs.getString("participants")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bill: " + e.getMessage());
        }

        return null;
    }
}