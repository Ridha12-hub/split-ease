import java.util.*;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        ExpenseSplitter splitter = new ExpenseSplitter();
        splitter.start();
    }
}

class ExpenseSplitter {
    private Scanner scanner;
    private Database db;
    
    public ExpenseSplitter() {
        scanner = new Scanner(System.in);
        db = new Database();
    }
    
    public void start() {
        while (true) {
            showMenu();
            int choice = getUserChoice();
            
            switch (choice) {
                case 1:
                    addNewExpense();
                    break;
                case 2:
                    viewExpenses();
                    break;
                case 3:
                    System.out.println("Thank you for using Expense Splitter!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void showMenu() {
        System.out.println("\n=== Expense Splitter Menu ===");
        System.out.println("1. Add New Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private int getUserChoice() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Clear the invalid input
            return 0;
        }
    }
    
    private void addNewExpense() {
        scanner.nextLine(); // Clear buffer
        
        try {
            // Get expense details
            System.out.println("\n=== Add New Expense ===");
            System.out.print("Enter expense description: ");
            String description = scanner.nextLine();
            
            System.out.print("Enter total amount (₹): ");
            double totalAmount = scanner.nextDouble();
            scanner.nextLine(); // Clear buffer
            
            // Choose split type
            System.out.println("\nChoose splitting method:");
            System.out.println("1. Equal Split");
            System.out.println("2. Custom Split");
            System.out.print("Enter your choice: ");
            int splitChoice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            
            System.out.print("Enter number of people: ");
            int numberOfPeople = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            
            // Create expense in database
            int expenseId = db.createExpense(description, totalAmount);
            
            if (splitChoice == 1) {
                handleEqualSplit(expenseId, totalAmount, numberOfPeople);
            } else {
                handleCustomSplit(expenseId, totalAmount, numberOfPeople);
            }
            
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter numeric values where required.");
            scanner.nextLine(); // Clear the invalid input
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void handleEqualSplit(int expenseId, double totalAmount, int numberOfPeople) throws SQLException {
        double equalShare = Math.round((totalAmount / numberOfPeople) * 100.0) / 100.0;
        double remainingAmount = totalAmount;
        
        for (int i = 0; i < numberOfPeople; i++) {
            System.out.print("Enter name for person " + (i + 1) + ": ");
            String name = scanner.nextLine();
            
            double amount;
            if (i == numberOfPeople - 1) {
                // Last person gets the remaining amount to handle rounding
                amount = remainingAmount;
            } else {
                amount = equalShare;
                remainingAmount -= equalShare;
            }
            
            db.createSplit(expenseId, name, amount);
            System.out.printf("%s will pay: $%.2f%n", name, amount);
        }
    }
    
    private void handleCustomSplit(int expenseId, double totalAmount, int numberOfPeople) throws SQLException {
        double remainingAmount = totalAmount;
        
        for (int i = 0; i < numberOfPeople; i++) {
            System.out.print("Enter name for person " + (i + 1) + ": ");
            String name = scanner.nextLine();
            
            double amount;
            if (i == numberOfPeople - 1) {
                // Last person gets the remaining amount
                amount = remainingAmount;
                System.out.printf("Final person %s will pay the remaining: $%.2f%n", name, amount);
            } else {
                while (true) {
                    System.out.printf("Enter amount for %s (remaining: $%.2f): ", name, remainingAmount);
                    amount = scanner.nextDouble();
                    scanner.nextLine(); // Clear buffer
                    
                    if (amount <= remainingAmount) {
                        break;
                    }
                    System.out.println("Amount too high! Please enter a valid amount.");
                }
                remainingAmount -= amount;
            }
            
            db.createSplit(expenseId, name, amount);
            System.out.printf("%s will pay: $%.2f%n", name, amount);
        }
    }
    
    private void viewExpenses() {
        try {
            List<ExpenseRecord> expenses = db.getAllExpenses();
            if (expenses.isEmpty()) {
                System.out.println("\nNo expenses found!");
                return;
            }
            
            for (ExpenseRecord expense : expenses) {
                System.out.println("\n=== Expense Details ===");
                System.out.println("ID: " + expense.id);
                System.out.println("Description: " + expense.description);
                System.out.printf("Total Amount: $%.2f%n", expense.amount);
                System.out.println("Date: " + expense.date);
                
                List<Split> splits = db.getSplitsForExpense(expense.id);
                System.out.println("\nSplits:");
                for (Split split : splits) {
                    System.out.printf("- %s: $%.2f%n", split.name, split.amount);
                }
                System.out.println("--------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving expenses: " + e.getMessage());
        }
    }
}