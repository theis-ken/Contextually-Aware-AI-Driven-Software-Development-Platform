package com.example;

public class SpendingLimit {
    private Category category;
    private double monthlyLimit;
    private double currentSpent;

    public SpendingLimit(Category category, double monthlyLimit) {
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.currentSpent = 0;
    }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public double getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(double monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public double getCurrentSpent() { return currentSpent; }
    public void setCurrentSpent(double currentSpent) { this.currentSpent = currentSpent; }
}