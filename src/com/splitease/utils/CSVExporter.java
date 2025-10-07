package com.splitease.utils;

import com.splitease.managers.ReminderSystem;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CSVExporter {
    private ReminderSystem reminderSystem;

    public CSVExporter(ReminderSystem reminderSystem) {
        this.reminderSystem = reminderSystem;
    }

    public void exportOverdueBills() {
        List<String[]> overduePayments = reminderSystem.getOverduePaymentsForCSV();

        if (overduePayments.isEmpty()) {
            System.out.println("No overdue bills to export.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String filename = "overdue_bills_" + timestamp + ".csv";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("Username,Amount,Payer,Due Date,Days Overdue,Description\n");

            for (String[] row : overduePayments) {
                writer.append(String.join(",", row));
                writer.append("\n");
            }

            System.out.println("Overdue bills exported successfully to: " + filename);

        } catch (IOException e) {
            System.err.println("Error exporting overdue bills: " + e.getMessage());
        }
    }
}