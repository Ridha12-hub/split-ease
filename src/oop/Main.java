package oop;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize the database on startup
        DatabaseHelper.initializeDatabase();
        
        UserManagement userManager = new UserManagement();
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n--- Split Ease Console Menu ---");
            System.out.println("1. Register a new user");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter username: ");
                    String regUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String regPassword = scanner.nextLine();
                    String regMessage = userManager.registerUser(regUsername, regPassword);
                    System.out.println(regMessage);
                    break;
                case "2":
                    System.out.print("Enter username: ");
                    String loginUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String loginPassword = scanner.nextLine();
                    if (userManager.loginUser(loginUsername, loginPassword)) {
                        System.out.println("✅ Login successful! Welcome, " + loginUsername + ".");
                        // A successful login would lead to the main app functionality
                    } else {
                        System.out.println("Error: Invalid username or password.");
                    }
                    break;
                case "3":
                    System.out.println("Exiting application. Goodbye!");
                    scanner.close();
                    return; // Exit the program
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}