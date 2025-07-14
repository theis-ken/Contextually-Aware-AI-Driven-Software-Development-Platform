// Prompt 4: Unit tests for Transaction
package com.example;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    @Test
    void testConstructorAndGetters() {
        Category cat = new Category("Groceries", CategoryType.EXPENSE);
        Transaction t = new Transaction("Milk", 5.0, LocalDate.of(2024, 1, 1), TransactionType.EXPENSE, cat);
        assertEquals("Milk", t.getDescription());
        assertEquals(5.0, t.getAmount());
        assertEquals(LocalDate.of(2024, 1, 1), t.getDate());
        assertEquals(TransactionType.EXPENSE, t.getType());
        assertEquals(cat, t.getCategory());
    }

    @Test
    void testSetters() {
        Category cat = new Category("Groceries", CategoryType.EXPENSE);
        Transaction t = new Transaction("", 0, LocalDate.now(), TransactionType.INCOME, null);
        t.setDescription("Eggs");
        t.setAmount(2.5);
        t.setDate(LocalDate.of(2024, 2, 2));
        t.setType(TransactionType.EXPENSE);
        t.setCategory(cat);
        assertEquals("Eggs", t.getDescription());
        assertEquals(2.5, t.getAmount());
        assertEquals(LocalDate.of(2024, 2, 2), t.getDate());
        assertEquals(TransactionType.EXPENSE, t.getType());
        assertEquals(cat, t.getCategory());
    }
} 