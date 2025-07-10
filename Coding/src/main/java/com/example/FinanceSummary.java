package com.example;

import java.util.Map;

public class FinanceSummary {
    private double totalIncome;
    private double totalExpenses;
    private double netBalance;
    private Map<Category, Double> categoryBreakdown;

    public FinanceSummary(double totalIncome, double totalExpenses, double netBalance, Map<Category, Double> categoryBreakdown) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netBalance = netBalance;
        this.categoryBreakdown = categoryBreakdown;
    }

    public double getTotalIncome() { return totalIncome; }
    public void setTotalIncome(double totalIncome) { this.totalIncome = totalIncome; }

    public double getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(double totalExpenses) { this.totalExpenses = totalExpenses; }

    public double getNetBalance() { return netBalance; }
    public void setNetBalance(double netBalance) { this.netBalance = netBalance; }

    public Map<Category, Double> getCategoryBreakdown() { return categoryBreakdown; }
    public void setCategoryBreakdown(Map<Category, Double> categoryBreakdown) { this.categoryBreakdown = categoryBreakdown; }
}
