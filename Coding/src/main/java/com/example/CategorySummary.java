// Prompt 2: CategorySummary class for table display
package com.example;

// JavaFX update: Class to hold category summary data for table display
public class CategorySummary {
    private String categoryName;
    private String type;
    private String total;
    private String spendingLimit;
    private String usagePercentage;
    
    public CategorySummary(String categoryName, String type, String total, String spendingLimit, String usagePercentage) {
        this.categoryName = categoryName;
        this.type = type;
        this.total = total;
        this.spendingLimit = spendingLimit;
        this.usagePercentage = usagePercentage;
    }
    
    // Getters for table display
    public String getCategoryName() { return categoryName; }
    public String getType() { return type; }
    public String getTotal() { return total; }
    public String getSpendingLimit() { return spendingLimit; }
    public String getUsagePercentage() { return usagePercentage; }
    
    // Setters for table updates
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setType(String type) { this.type = type; }
    public void setTotal(String total) { this.total = total; }
    public void setSpendingLimit(String spendingLimit) { this.spendingLimit = spendingLimit; }
    public void setUsagePercentage(String usagePercentage) { this.usagePercentage = usagePercentage; }
} 