// Prompt 5: Unit tests for SpendingLimit
package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpendingLimitTest {
    @Test
    void testConstructorAndGetters() {
        Category cat = new Category("Groceries", CategoryType.EXPENSE);
        SpendingLimit sl = new SpendingLimit(cat, 100.0);
        assertEquals(cat, sl.getCategory());
        assertEquals(100.0, sl.getMonthlyLimit());
        assertEquals(0.0, sl.getCurrentSpent());
    }

    @Test
    void testSetters() {
        Category cat = new Category("Groceries", CategoryType.EXPENSE);
        SpendingLimit sl = new SpendingLimit(cat, 0);
        sl.setMonthlyLimit(200.0);
        sl.setCurrentSpent(50.0);
        assertEquals(200.0, sl.getMonthlyLimit());
        assertEquals(50.0, sl.getCurrentSpent());
    }
} 