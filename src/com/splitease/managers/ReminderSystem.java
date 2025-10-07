package com.splitease.managers;

import com.splitease.database.DatabaseHelper;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ReminderSystem {
    private DatabaseHelper dbHelper;

    public ReminderSystem() {
        this.dbHelper = DatabaseHelper.getInstance();
    }

    public void checkReminders() {
        updateOverduePayments();
        displayPendingPayments();
        displayOverduePayments();
    }

    private void updateOverduePayments() {
        String query = "UPDATE bill_shares SET status = 'Overdue' WHERE status = 'Pending' AND date(due_date) < date('now')";
        
        try (Connection conn = dbHelper.getConnection();
             Statement stmt = conn.createStatement()) {
            
            int updated = stmt.executeUpdate(query);
            if (updated > 0) {
                System.out.println(updated + " payment(s) marked as overdue.\n");
            }
        } catch (SQLException e) {
            System.err.println("Error updating overdue payments: " + e.getMessage());
        }
    }

    public void displayPendingPayments() {
        String query = """
            SELECT bs.username, bs.share_amount, bs.due_date, b.description, b.payer_username
            FROM bill_shares bs
            JOIN bills b ON bs.bill_id = b.id
            WHERE bs.status = 'Pending'
            ORDER BY bs.due_date
        """;

        try (Connection conn = dbHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            boolean hasPending = false;
            System.out.println("\n" + "=".repeat(100));
            System.out.println("PENDING PAYMENTS");
            System.out.println("=".repeat(100));

            while (rs.next()) {
                hasPending = true;
                String username = rs.getString("username");
                double amount = rs.getDouble("share_amount");
                String dueDate = rs.getString("due_date");
                String description = rs.getString("description");
                String payer = rs.getString("payer_username");

                LocalDate due = LocalDate.parse(dueDate);
                long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), due);

                System.out.printf("  %s owes $%.2f to %s | Due: %s (%d days) | Bill: %s\n",
                        username, amount, payer, dueDate, daysUntilDue, description);
            }

            if (!hasPending) {
                System.out.println("  No pending payments.");
            }

            System.out.println("=".repeat(100) + "\n");

        } catch (SQLException e) {
            System.err.println("Error fetching pending payments: " + e.getMessage());
        }
    }

    public void displayOverduePayments() {
        String query = """
            SELECT bs.username, bs.share_amount, bs.due_date, b.description, b.payer_username
            FROM bill_shares bs
            JOIN bills b ON bs.bill_id = b.id
            WHERE bs.status = 'Overdue'
            ORDER BY bs.due_date
        """;

        try (Connection conn = dbHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            boolean hasOverdue = false;
            System.out.println("\n" + "=".repeat(100));
            System.out.println("OVERDUE PAYMENTS");
            System.out.println("=".repeat(100));

            while (rs.next()) {
                hasOverdue = true;
                String username = rs.getString("username");
                double amount = rs.getDouble("share_amount");
                String dueDate = rs.getString("due_date");
                String description = rs.getString("description");
                String payer = rs.getString("payer_username");

                LocalDate due = LocalDate.parse(dueDate);
                long daysOverdue = ChronoUnit.DAYS.between(due, LocalDate.now());

                System.out.printf("  %s owes $%.2f to %s | Was Due: %s (%d days overdue) | Bill: %s\n",
                        username, amount, payer, dueDate, daysOverdue, description);
            }

            if (!hasOverdue) {
                System.out.println("  No overdue payments.");
            }

            System.out.println("=".repeat(100) + "\n");

        } catch (SQLException e) {
            System.err.println("Error fetching overdue payments: " + e.getMessage());
        }
    }

    public List<String[]> getOverduePaymentsForCSV() {
        List<String[]> data = new ArrayList<>();
        String query = """
            SELECT bs.username, bs.share_amount, bs.due_date, b.description, b.payer_username
            FROM bill_shares bs
            JOIN bills b ON bs.bill_id = b.id
            WHERE bs.status = 'Overdue'
            ORDER BY bs.due_date
        """;

        try (Connection conn = dbHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String username = rs.getString("username");
                double amount = rs.getDouble("share_amount");
                String dueDate = rs.getString("due_date");
                String description = rs.getString("description");
                String payer = rs.getString("payer_username");

                LocalDate due = LocalDate.parse(dueDate);
                long daysOverdue = ChronoUnit.DAYS.between(due, LocalDate.now());

                data.add(new String[]{
                    username,
                    String.format("%.2f", amount),
                    payer,
                    dueDate,
                    String.valueOf(daysOverdue),
                    description
                });
            }
        } catch (SQLException e) {
            System.err.println("Error fetching overdue payments for CSV: " + e.getMessage());
        }

        return data;
    }
}