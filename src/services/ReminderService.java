package services;

import models.BillReminder;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReminderService {

    // Add a new bill
    public static void addBill(String userName, double amount, String dueDate) {
        String sql = "INSERT INTO bills(userName, amount, paid, dueDate) VALUES (?, ?, 0, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, dueDate);
            pstmt.executeUpdate();
            System.out.println("Bill added for " + userName);
        } catch (SQLException e) {
            System.out.println("Error adding bill: " + e.getMessage());
        }
    }

    // Mark a bill as paid
    public static void markAsPaid(int billId) {
        String sql = "UPDATE bills SET paid = 1 WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            pstmt.executeUpdate();
            System.out.println("Bill " + billId + " marked as paid.");
        } catch (SQLException e) {
            System.out.println("Error marking bill as paid: " + e.getMessage());
        }
    }

    // Fetch unpaid bills
    public static List<BillReminder> fetchUnpaidBills() {
        List<BillReminder> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE paid = 0";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bills.add(new BillReminder(
                        rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getDouble("amount"),
                        rs.getInt("paid"),
                        rs.getString("dueDate")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching bills: " + e.getMessage());
        }
        return bills;
    }

    // Remove fully paid users (optional cleanup)
    public static void removeFullyPaidUsers() {
        String sql = "DELETE FROM bills WHERE paid = 1";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Error cleaning fully paid bills: " + e.getMessage());
        }
    }

    // Check reminders and overdue bills
    public static void checkReminders() {
        List<BillReminder> bills = fetchUnpaidBills();
        if (bills.isEmpty()) {
            System.out.println("All bills are paid!");
            return;
        }

        LocalDate today = LocalDate.now();
        System.out.println("=== Unpaid Bills / Reminders ===");
        for (BillReminder bill : bills) {
            LocalDate due = LocalDate.parse(bill.getDueDate());
            String status = due.isBefore(today) ? "OVERDUE" : "Pending";
            System.out.printf("ID: %d | User: %s | Amount: %.2f | Due: %s | Status: %s%n",
                    bill.getId(), bill.getUserName(), bill.getAmount(), bill.getDueDate(), status);
        }
    }
}
