package models;

public class BillReminder {
    private int id;
    private String userName;
    private double amount;
    private int paid; // 0 = unpaid, 1 = paid
    private String dueDate; // format: YYYY-MM-DD

    public BillReminder(int id, String userName, double amount, int paid, String dueDate) {
        this.id = id;
        this.userName = userName;
        this.amount = amount;
        this.paid = paid;
        this.dueDate = dueDate;
    }

    public int getId() { return id; }
    public String getUserName() { return userName; }
    public double getAmount() { return amount; }
    public int getPaid() { return paid; }
    public String getDueDate() { return dueDate; }

    public void setPaid(int paid) { this.paid = paid; }
}
