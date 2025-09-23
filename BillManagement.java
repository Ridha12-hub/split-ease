import java.sql.*;
import java.util.Scanner;

public class BillManagement {
    private static final String DB_URL = "jdbc:sqlite:billshare.db";

    // Create Users table if not exists
    public static void createUserTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            );
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Users table ready.");
        } catch (SQLException e) {
            System.out.println("Error creating users table: " + e.getMessage());
        }
    }

    // Insert a sample user (Alice) if not already exists
    public static void insertSampleUser() {
        String sql = "INSERT OR IGNORE INTO users(username, password) VALUES('Alice', '1234')";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Sample user Alice ready (user_id = 1).");
        } catch (SQLException e) {
            System.out.println("Error inserting sample user: " + e.getMessage());
        }
    }

    // Register a new user
    public static void registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            System.out.println("User " + username + " registered successfully!");
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }

    // View all users
    public static void listUsers() {
        String sql = "SELECT user_id, username FROM users";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== Registered Users ===");
            while (rs.next()) {
                System.out.printf("User ID: %d | Username: %s%n",
                        rs.getInt("user_id"),
                        rs.getString("username"));
            }
        } catch (SQLException e) {
            System.out.println("Error listing users: " + e.getMessage());
        }
    }

    // Delete user by ID
    public static void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("No user found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    // Update user details
    public static void updateUser(int userId, String newUsername, String newPassword) {
        String sql = "UPDATE users SET username = ?, password = ? WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername);
            pstmt.setString(2, newPassword);
            pstmt.setInt(3, userId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("No user found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    // Create Bills table if not exists
    public static void createBillTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS bills (
                bill_id INTEGER PRIMARY KEY AUTOINCREMENT,
                amount REAL NOT NULL,
                payer_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                description TEXT,
                FOREIGN KEY (payer_id) REFERENCES users(user_id)
            );
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Bills table ready.");
        } catch (SQLException e) {
            System.out.println("Error creating bills table: " + e.getMessage());
        }
    }

    // Insert a new bill
    public static void addBill(double amount, int payerId, String date, String description) {
        String sql = "INSERT INTO bills(amount, payer_id, date, description) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setInt(2, payerId);
            pstmt.setString(3, date);
            pstmt.setString(4, description);
            pstmt.executeUpdate();

            System.out.println("Bill added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding bill: " + e.getMessage());
        }
    }

    // List all bills
    public static void listBills() {
        String sql = "SELECT b.bill_id, b.amount, u.username AS payer, b.date, b.description " +
                     "FROM bills b JOIN users u ON b.payer_id = u.user_id";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== All Bills ===");
            while (rs.next()) {
                System.out.printf("Bill ID: %d | Amount: %.2f | Payer: %s | Date: %s | Description: %s%n",
                        rs.getInt("bill_id"),
                        rs.getDouble("amount"),
                        rs.getString("payer"),
                        rs.getString("date"),
                        rs.getString("description"));
            }
        } catch (SQLException e) {
            System.out.println("Error listing bills: " + e.getMessage());
        }
    }

    // Delete bill by ID
    public static void deleteBill(int billId) {
        String sql = "DELETE FROM bills WHERE bill_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Bill deleted successfully.");
            } else {
                System.out.println("No bill found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting bill: " + e.getMessage());
        }
    }

    // Update bill details
    public static void updateBill(int billId, double amount, int payerId, String date, String description) {
        String sql = "UPDATE bills SET amount = ?, payer_id = ?, date = ?, description = ? WHERE bill_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, payerId);
            pstmt.setString(3, date);
            pstmt.setString(4, description);
            pstmt.setInt(5, billId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Bill updated successfully.");
            } else {
                System.out.println("No bill found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating bill: " + e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {
        // Setup
        createUserTable();
        insertSampleUser();
        createBillTable();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Register User");
            System.out.println("2. View Users");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. Add Bill");
            System.out.println("6. View Bills");
            System.out.println("7. Update Bill");
            System.out.println("8. Delete Bill");
            System.out.println("9. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter username: ");
                    String username = sc.nextLine();
                    System.out.print("Enter password: ");
                    String password = sc.nextLine();
                    registerUser(username, password);
                }
                case 2 -> listUsers();
                case 3 -> {
                    listUsers();
                    System.out.print("Enter user ID to update: ");
                    int uid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter new username: ");
                    String uname = sc.nextLine();
                    System.out.print("Enter new password: ");
                    String pass = sc.nextLine();
                    updateUser(uid, uname, pass);
                }
                case 4 -> {
                    listUsers();
                    System.out.print("Enter user ID to delete: ");
                    int uid = sc.nextInt();
                    sc.nextLine();
                    deleteUser(uid);
                }
                case 5 -> {
                    listUsers(); // show available payers
                    System.out.print("Enter amount: ");
                    double amount = sc.nextDouble();
                    sc.nextLine();

                    System.out.print("Enter payer's user ID: ");
                    int payerId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter date (YYYY-MM-DD): ");
                    String date = sc.nextLine();

                    System.out.print("Enter description: ");
                    String desc = sc.nextLine();

                    addBill(amount, payerId, date, desc);
                }
                case 6 -> listBills();
                case 7 -> {
                    listBills();
                    System.out.print("Enter bill ID to update: ");
                    int bid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter new amount: ");
                    double amt = sc.nextDouble();
                    sc.nextLine();
                    System.out.print("Enter new payer ID: ");
                    int pid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter new date (YYYY-MM-DD): ");
                    String newDate = sc.nextLine();
                    System.out.print("Enter new description: ");
                    String newDesc = sc.nextLine();
                    updateBill(bid, amt, pid, newDate, newDesc);
                }
                case 8 -> {
                    listBills();
                    System.out.print("Enter bill ID to delete: ");
                    int bid = sc.nextInt();
                    sc.nextLine();
                    deleteBill(bid);
                }
                case 9 -> {
                    System.out.println("Exiting Bill Management...");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }
}