// Class to store expense record details
class ExpenseRecord {
    public final int id;
    public final String description;
    public final double amount;
    public final String date;
    
    public ExpenseRecord(int id, String description, double amount, String date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }
}

// Class to store split details
class Split {
    public final String name;
    public final double amount;
    
    public Split(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }
}