package com.splitease.managers;

import com.splitease.database.DatabaseHelper;
import com.splitease.models.Bill;
import java.sql.*;
import java.time.LocalDate;
import java.util.Map;

public class ExpenseSplitter {
    private DatabaseHelper dbHelper;
    private BillManager billManager;

    public ExpenseSplitter(BillManager billManager) {
        this.dbHelper = DatabaseHelper.getInstance();
        this.billManager = billManager;
    }

    public boolean splitEqually(int billId, int dueDays) {
        Bill bill = billManager.getBillById(billId);
        
        if (bill == null) {
            System.out.println("Bill not found.");
            return false;
        }

        String[] participantsArray = bill.getParticipants().split(", ");
        int numParticipants = participantsArray.length;
        double baseShare = Math.floor(bill.getAmount() * 100 / numParticipants) / 100.0;
        double totalAllocated = baseShare * numParticipants;
        double remainder = Math.round((bill.getAmount() - totalAllocated) * 100.0) / 100.0;

        Map<String, Double> shares = new java.util.HashMap<>();
        for (String participant : participantsArray) {
            shares.put(participant, baseShare);
        }

        if (remainder > 0) {
            double payerShare = shares.get(bill.getPayerUsername());
            shares.put(bill.getPayerUsername(), payerShare + remainder);
        }

        return saveShares(billId, shares, dueDays);
    }

    public boolean splitManually(int billId, Map<String, Double> shares, int dueDays) {
        Bill bill = billManager.getBillById(billId);
        
        if (bill == null) {
            System.out.println("Bill not found.");
            return false;
        }

        double totalShares = shares.values().stream().mapToDouble(Double::doubleValue).sum();
        double difference = Math.abs(bill.getAmount() - totalShares);

        if (difference > 0.01) {
            System.out.printf("Error: Total shares ($%.2f) do not match bill amount ($%.2f).\n",
                    totalShares, bill.getAmount());
            return false;
        }

        return saveShares(billId, shares, dueDays);
    }

    private boolean saveShares(int billId, Map<String, Double> shares, int dueDays) {
        String query = "INSERT INTO bill_shares (bill_id, username, share_amount, due_date, status) VALUES (?, ?, ?, ?, 'Pending')";
        LocalDate dueDate = LocalDate.now().plusDays(dueDays);

        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                for (Map.Entry<String, Double> entry : shares.entrySet()) {
                    pstmt.setInt(1, billId);
                    pstmt.setString(2, entry.getKey());
                    pstmt.setDouble(3, entry.getValue());
                    pstmt.setString(4, dueDate.toString());
                    pstmt.executeUpdate();
                }
            }

            conn.commit();
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("Expense split successfully for Bill ID: " + billId);
            System.out.println("Due Date: " + dueDate);
            System.out.println("-".repeat(60));
            for (Map.Entry<String, Double> entry : shares.entrySet()) {
                System.out.printf("  %s owes: $%.2f\n", entry.getKey(), entry.getValue());
            }
            System.out.println("=".repeat(60) + "\n");
            
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            System.err.println("Error saving shares: " + e.getMessage());
            return false;
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
}