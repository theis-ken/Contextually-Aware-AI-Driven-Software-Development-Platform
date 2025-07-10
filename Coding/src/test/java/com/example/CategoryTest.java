// Prompt 5: Unit tests for Category
package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {
    @Test
    void testConstructorAndGetters() {
        Category c = new Category("Books", CategoryType.EXPENSE);
        assertEquals("Books", c.getName());
        assertEquals(CategoryType.EXPENSE, c.getType());
    }

    @Test
    void testSetters() {
        Category c = new Category("", CategoryType.INCOME);
        c.setName("Salary");
        c.setType(CategoryType.INCOME);
        assertEquals("Salary", c.getName());
        assertEquals(CategoryType.INCOME, c.getType());
    }
} 