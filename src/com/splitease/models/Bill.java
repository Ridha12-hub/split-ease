package com.splitease.models;

public class Bill {
    private int id;
    private double amount;
    private String payerUsername;
    private String date;
    private String description;
    private String participants;

    public Bill(int id, double amount, String payerUsername, String date, String description, String participants) {
        this.id = id;
        this.amount = amount;
        this.payerUsername = payerUsername;
        this.date = date;
        this.description = description;
        this.participants = participants;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getPayerUsername() {
        return payerUsername;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getParticipants() {
        return participants;
    }

    @Override
    public String toString() {
        return String.format("Bill ID: %d | Amount: $%.2f | Payer: %s | Date: %s | Description: %s | Participants: %s",
                id, amount, payerUsername, date, description, participants);
    }
}