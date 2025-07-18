// Prompt 3: Moved to com.example package for Maven structure
package com.example;

//Prompt 1: Core logic
public class Category {
    private String name;
    private CategoryType type; // EXPENSE or INCOME

    public Category(String name, CategoryType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }
}