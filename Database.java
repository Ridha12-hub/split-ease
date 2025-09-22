import java.sql.*;
import java.util.*;

class Database {
    private Connection connection;
    
    public Database() {
        setupDatabase();
    }
    
    private void setupDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:expenses.db");
            createTables();
        } catch (Exception e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }
    
    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Create Expenses table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    description TEXT NOT NULL,
                    amount DECIMAL(10,2) NOT NULL,
                    date DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);
            
            // Create Splits table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS splits (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    expense_id INTEGER,
                    person_name TEXT NOT NULL,
                    amount DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (expense_id) REFERENCES expenses(id)
                )
            """);
        }
    }
    
    public int createExpense(String description, double amount) throws SQLException {
        String sql = "INSERT INTO expenses (description, amount) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, description);
            pstmt.setDouble(2, amount);
            pstmt.executeUpdate();
            
            // SQLite specific way to get the last inserted id
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("Failed to create expense record");
        }
    }
    
    public void createSplit(int expenseId, String personName, double amount) throws SQLException {
        String sql = "INSERT INTO splits (expense_id, person_name, amount) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, expenseId);
            pstmt.setString(2, personName);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        }
    }
    
    public List<ExpenseRecord> getAllExpenses() throws SQLException {
        List<ExpenseRecord> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses ORDER BY date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ExpenseRecord expense = new ExpenseRecord(
                    rs.getInt("id"),
                    rs.getString("description"),
                    rs.getDouble("amount"),
                    rs.getString("date")
                );
                expenses.add(expense);
            }
        }
        return expenses;
    }
    
    public List<Split> getSplitsForExpense(int expenseId) throws SQLException {
        List<Split> splits = new ArrayList<>();
        String sql = "SELECT * FROM splits WHERE expense_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, expenseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Split split = new Split(
                    rs.getString("person_name"),
                    rs.getDouble("amount")
                );
                splits.add(split);
            }
        }
        return splits;
    }
}