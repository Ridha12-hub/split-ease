package controllers;

import services.ReminderService;
import java.util.Scanner;

public class ReminderController {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== SplitEase Reminder Menu ===");
            System.out.println("1. Add a new bill");
            System.out.println("2. Mark a bill as paid");
            System.out.println("3. Show reminders / overdue bills");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter user name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter amount: ");
                    double amt = sc.nextDouble();
                    sc.nextLine();
                    System.out.print("Enter due date (YYYY-MM-DD): ");
                    String due = sc.nextLine();
                    ReminderService.addBill(name, amt, due);
                    break;
                case 2:
                    System.out.print("Enter bill ID to mark as paid: ");
                    int id = sc.nextInt();
                    ReminderService.markAsPaid(id);
                    break;
                case 3:
                    ReminderService.checkReminders();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
