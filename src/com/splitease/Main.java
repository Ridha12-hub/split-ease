package com.splitease;

import com.splitease.database.DatabaseHelper;
import com.splitease.managers.*;
import com.splitease.utils.CSVExporter;
import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static UserManager userManager = new UserManager();
    private static BillManager billManager = new BillManager();
    private static ExpenseSplitter expenseSplitter = new ExpenseSplitter(billManager);
    private static ReminderSystem reminderSystem = new ReminderSystem();
    private static CSVExporter csvExporter = new CSVExporter(reminderSystem);

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║      Welcome to SplitEase!           ║");
        System.out.println("║   Your Expense Splitting Companion   ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();

        DatabaseHelper.getInstance().initializeDatabase();

        while (!userManager.isLoggedIn()) {
            showAuthMenu();
        }

        while (userManager.isLoggedIn()) {
            showMainMenu();
        }

        System.out.println("\nThank you for using SplitEase! Goodbye!");
        scanner.close();
    }

    private static void showAuthMenu() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║         Authentication Menu          ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║  1. Register                         ║");
        System.out.println("║  2. Login                            ║");
        System.out.println("║  3. Exit                             ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleRegistration();
                break;
            case "2":
                handleLogin();
                break;
            case "3":
                System.out.println("\nThank you for using SplitEase! Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void showMainMenu() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║            Main Menu                 ║");
        System.out.println("║  Logged in as: " + String.format("%-20s", userManager.getCurrentUser()) + " ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║  1. Add Bill                         ║");
        System.out.println("║  2. View All Bills                   ║");
        System.out.println("║  3. Split Expenses                   ║");
        System.out.println("║  4. Check Reminders                  ║");
        System.out.println("║  5. Export Overdue Bills (CSV)       ║");
        System.out.println("║  6. Logout                           ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleAddBill();
                break;
            case "2":
                billManager.displayAllBills();
                break;
            case "3":
                handleSplitExpenses();
                break;
            case "4":
                reminderSystem.checkReminders();
                break;
            case "5":
                csvExporter.exportOverdueBills();
                break;
            case "6":
                userManager.logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void handleRegistration() {
        System.out.println("\n--- User Registration ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        userManager.register(username, password, email);
    }

    private static void handleLogin() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        userManager.login(username, password);
    }

    private static void handleAddBill() {
        System.out.println("\n--- Add New Bill ---");
        
        System.out.print("Enter bill amount: $");
        String amountStr = scanner.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
            return;
        }

        System.out.print("Enter bill description: ");
        String description = scanner.nextLine().trim();

        List<String> allUsers = DatabaseHelper.getInstance().getAllUsernames();
        if (allUsers.isEmpty()) {
            System.out.println("No users available. Please register users first.");
            return;
        }

        System.out.println("\nAvailable users: " + String.join(", ", allUsers));

        System.out.print("Enter payer username: ");
        String payer = scanner.nextLine().trim();

        if (!allUsers.contains(payer)) {
            System.out.println("Payer not found in user list.");
            return;
        }

        System.out.print("Enter participant usernames (comma-separated): ");
        String participantsInput = scanner.nextLine().trim();
        List<String> participants = Arrays.asList(participantsInput.split("\\s*,\\s*"));

        for (String participant : participants) {
            if (!allUsers.contains(participant)) {
                System.out.println("Participant '" + participant + "' not found in user list.");
                return;
            }
        }

        int billId = billManager.createBill(amount, payer, description, participants);

        if (billId > 0) {
            System.out.print("\nWould you like to split this bill now? (yes/no): ");
            String splitNow = scanner.nextLine().trim().toLowerCase();
            if (splitNow.equals("yes") || splitNow.equals("y")) {
                splitBill(billId);
            }
        }
    }

    private static void handleSplitExpenses() {
        billManager.displayAllBills();
        
        System.out.print("\nEnter Bill ID to split: ");
        String billIdStr = scanner.nextLine().trim();
        int billId;
        try {
            billId = Integer.parseInt(billIdStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Bill ID. Please enter a valid number.");
            return;
        }

        splitBill(billId);
    }

    private static void splitBill(int billId) {
        System.out.println("\n--- Split Expense Options ---");
        System.out.println("1. Split Equally");
        System.out.println("2. Split Manually");
        System.out.print("Choose splitting method: ");
        String method = scanner.nextLine().trim();

        System.out.print("Enter number of days until due: ");
        String dueDaysStr = scanner.nextLine().trim();
        int dueDays;
        try {
            dueDays = Integer.parseInt(dueDaysStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of days. Please enter a valid number.");
            return;
        }

        switch (method) {
            case "1":
                expenseSplitter.splitEqually(billId, dueDays);
                break;
            case "2":
                handleManualSplit(billId, dueDays);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private static void handleManualSplit(int billId, int dueDays) {
        com.splitease.models.Bill bill = billManager.getBillById(billId);
        if (bill == null) {
            System.out.println("Bill not found.");
            return;
        }

        String[] participants = bill.getParticipants().split(", ");
        Map<String, Double> shares = new HashMap<>();

        System.out.println("\nTotal bill amount: $" + bill.getAmount());
        System.out.println("Enter share amount for each participant:");

        for (String participant : participants) {
            System.out.print(participant + ": $");
            String shareStr = scanner.nextLine().trim();
            try {
                double share = Double.parseDouble(shareStr);
                shares.put(participant, share);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount for " + participant + ". Using $0.00");
                shares.put(participant, 0.0);
            }
        }

        expenseSplitter.splitManually(billId, shares, dueDays);
    }
}
