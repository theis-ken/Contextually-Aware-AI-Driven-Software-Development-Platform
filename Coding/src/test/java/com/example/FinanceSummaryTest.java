// Prompt 5: Unit tests for FinanceSummary
package com.example;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class FinanceSummaryTest {
    @Test
    void testConstructorAndGetters() {
        Map<Category, Double> breakdown = new HashMap<>();
        Category cat = new Category("Groceries", CategoryType.EXPENSE);
        breakdown.put(cat, 50.0);
        FinanceSummary fs = new FinanceSummary(100.0, 50.0, 50.0, breakdown);
        assertEquals(100.0, fs.getTotalIncome());
        assertEquals(50.0, fs.getTotalExpenses());
        assertEquals(50.0, fs.getNetBalance());
        assertEquals(breakdown, fs.getCategoryBreakdown());
    }

    @Test
    void testSetters() {
        FinanceSummary fs = new FinanceSummary(0, 0, 0, new HashMap<>());
        fs.setTotalIncome(200.0);
        fs.setTotalExpenses(100.0);
        fs.setNetBalance(100.0);
        Map<Category, Double> breakdown = new HashMap<>();
        Category cat = new Category("Books", CategoryType.EXPENSE);
        breakdown.put(cat, 30.0);
        fs.setCategoryBreakdown(breakdown);
        assertEquals(200.0, fs.getTotalIncome());
        assertEquals(100.0, fs.getTotalExpenses());
        assertEquals(100.0, fs.getNetBalance());
        assertEquals(breakdown, fs.getCategoryBreakdown());
    }
} 