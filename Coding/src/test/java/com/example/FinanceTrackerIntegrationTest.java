// Prompt 6: Integration tests for FinanceTracker
package com.example;

import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class FinanceTrackerIntegrationTest {
    private FinanceTracker tracker;
    private Category groceries, salary, books;

    @BeforeEach
    void setUp() {
        tracker = new FinanceTracker();
        groceries = new Category("Groceries", CategoryType.EXPENSE);
        salary = new Category("Salary", CategoryType.INCOME);
        books = new Category("Books", CategoryType.EXPENSE);
        tracker.addCategory(groceries);
        tracker.addCategory(salary);
        tracker.addCategory(books);
    }

    @Test
    void testSpendingLimitUpdateOnExpense() {
        tracker.setSpendingLimit(groceries, 100.0);
        Transaction t = new Transaction("Eggs", 25.0, LocalDate.now(), TransactionType.EXPENSE, groceries);
        tracker.addTransaction(t);
        SpendingLimit sl = tracker.getSpendingLimits().stream().filter(l -> l.getCategory().equals(groceries)).findFirst().orElse(null);
        assertNotNull(sl);
        assertEquals(25.0, sl.getCurrentSpent());
    }

    @Test
    void testPersistenceAndDataIntegrity() {
        tracker.setSpendingLimit(groceries, 100.0);
        tracker.addTransaction(new Transaction("Eggs", 25.0, LocalDate.now(), TransactionType.EXPENSE, groceries));
        tracker.addTransaction(new Transaction("Pay", 2000.0, LocalDate.now(), TransactionType.INCOME, salary));
        tracker.addTransaction(new Transaction("Book", 15.0, LocalDate.now(), TransactionType.EXPENSE, books));
        String file = "integration_finance_data.json";
        tracker.saveToFile(file);
        FinanceTracker loaded = new FinanceTracker();
        loaded.loadFromFile(file);
        assertEquals(3, loaded.getTransactions().size());
        assertEquals(3, loaded.getCategories().size());
        assertEquals(1, loaded.getSpendingLimits().size());
        // Check links
        Transaction loadedEggs = loaded.getTransactions().stream().filter(t -> "Eggs".equals(t.getDescription())).findFirst().orElse(null);
        assertNotNull(loadedEggs);
        assertEquals("Groceries", loadedEggs.getCategory().getName());
        // Clean up
        new java.io.File(file).delete();
    }

    @Test
    void testCategoryDeletionPropagation() {
        tracker.setSpendingLimit(books, 50.0);
        Transaction t = new Transaction("Novel", 20.0, LocalDate.now(), TransactionType.EXPENSE, books);
        tracker.addTransaction(t);
        tracker.deleteCategory(books);
        assertFalse(tracker.getCategories().contains(books));
        assertNull(tracker.getTransactions().get(0).getCategory());
        assertTrue(tracker.getSpendingLimits().stream().noneMatch(sl -> books.equals(sl.getCategory())));
    }

    @Test
    void testMonthlySummaryAccuracy() {
        tracker.addTransaction(new Transaction("Pay", 2000.0, LocalDate.of(2024, 1, 1), TransactionType.INCOME, salary));
        tracker.addTransaction(new Transaction("Bread", 10.0, LocalDate.of(2024, 1, 2), TransactionType.EXPENSE, groceries));
        tracker.addTransaction(new Transaction("Book", 15.0, LocalDate.of(2024, 2, 1), TransactionType.EXPENSE, books));
        FinanceSummary jan = tracker.getMonthlySummary(YearMonth.of(2024, 1));
        FinanceSummary feb = tracker.getMonthlySummary(YearMonth.of(2024, 2));
        assertEquals(2000.0, jan.getTotalIncome());
        assertEquals(10.0, jan.getTotalExpenses());
        assertEquals(1990.0, jan.getNetBalance());
        assertEquals(0.0, feb.getTotalIncome());
        assertEquals(15.0, feb.getTotalExpenses());
        assertEquals(-15.0, feb.getNetBalance());
    }

    @Test
    void testTransactionFiltering() {
        Transaction t1 = new Transaction("A", 10, LocalDate.of(2024, 1, 1), TransactionType.EXPENSE, groceries);
        Transaction t2 = new Transaction("B", 20, LocalDate.of(2024, 2, 1), TransactionType.EXPENSE, books);
        tracker.addTransaction(t1);
        tracker.addTransaction(t2);
        List<Transaction> filtered = tracker.filterTransactions(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31), groceries);
        assertTrue(filtered.contains(t1));
        assertFalse(filtered.contains(t2));
    }

    @Test
    void testSpendingLimitWarnings() {
        tracker.setSpendingLimit(groceries, 100.0);
        // Capture System.out
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        System.setOut(new java.io.PrintStream(out));
        tracker.addTransaction(new Transaction("Eggs", 80.0, LocalDate.now(), TransactionType.EXPENSE, groceries));
        tracker.checkSpendingLimits();
        tracker.addTransaction(new Transaction("Milk", 25.0, LocalDate.now(), TransactionType.EXPENSE, groceries));
        tracker.checkSpendingLimits();
        System.setOut(orig);
        String output = out.toString();
        assertTrue(output.contains("80% of spending limit"));
        assertTrue(output.contains("Spending limit exceeded"));
    }

    @Test
    void testCategoryRenamePropagation() {
        tracker.setSpendingLimit(groceries, 100.0);
        Transaction t = new Transaction("Eggs", 10.0, LocalDate.now(), TransactionType.EXPENSE, groceries);
        tracker.addTransaction(t);
        tracker.renameCategory(groceries, "Food");
        assertEquals("Food", groceries.getName());
        assertEquals("Food", tracker.getTransactions().get(0).getCategory().getName());
        SpendingLimit sl = tracker.getSpendingLimits().stream().filter(l -> l.getCategory().getName().equals("Food")).findFirst().orElse(null);
        assertNotNull(sl);
    }
} 