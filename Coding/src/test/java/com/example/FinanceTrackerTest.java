// Prompt 4: Unit tests for FinanceTracker
package com.example;

import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FinanceTrackerTest {
    private FinanceTracker tracker;
    private Category groceries;
    private Category salary;

    @BeforeEach
    void setUp() {
        tracker = new FinanceTracker();
        groceries = new Category("Groceries", CategoryType.EXPENSE);
        salary = new Category("Salary", CategoryType.INCOME);
        tracker.addCategory(groceries);
        tracker.addCategory(salary);
    }

    @Test
    void testAddAndRemoveTransaction() {
        Transaction t = new Transaction("Milk", 5.0, LocalDate.now(), TransactionType.EXPENSE, groceries);
        tracker.addTransaction(t);
        assertTrue(tracker.getTransactions().contains(t));
        tracker.removeTransaction(t);
        assertFalse(tracker.getTransactions().contains(t));
    }

    @Test
    void testAddAndRemoveCategory() {
        Category books = new Category("Books", CategoryType.EXPENSE);
        tracker.addCategory(books);
        assertTrue(tracker.getCategories().contains(books));
        tracker.deleteCategory(books);
        assertFalse(tracker.getCategories().contains(books));
    }

    @Test
    void testSetAndCheckSpendingLimit() {
        tracker.setSpendingLimit(groceries, 100.0);
        Transaction t = new Transaction("Eggs", 90.0, LocalDate.now(), TransactionType.EXPENSE, groceries);
        tracker.addTransaction(t);
        tracker.checkSpendingLimits();
        SpendingLimit sl = tracker.getSpendingLimits().stream().filter(l -> l.getCategory().equals(groceries)).findFirst().orElse(null);
        assertNotNull(sl);
        assertEquals(90.0, sl.getCurrentSpent());
    }

    @Test
    void testGetMonthlySummary() {
        tracker.addTransaction(new Transaction("Pay", 2000.0, LocalDate.now(), TransactionType.INCOME, salary));
        tracker.addTransaction(new Transaction("Bread", 10.0, LocalDate.now(), TransactionType.EXPENSE, groceries));
        FinanceSummary summary = tracker.getMonthlySummary(YearMonth.now());
        assertEquals(2000.0, summary.getTotalIncome());
        assertEquals(10.0, summary.getTotalExpenses());
        assertEquals(1990.0, summary.getNetBalance());
    }

    @Test
    void testFilterTransactions() {
        Transaction t1 = new Transaction("A", 10, LocalDate.of(2024, 1, 1), TransactionType.EXPENSE, groceries);
        Transaction t2 = new Transaction("B", 20, LocalDate.of(2024, 2, 1), TransactionType.EXPENSE, groceries);
        tracker.addTransaction(t1);
        tracker.addTransaction(t2);
        List<Transaction> filtered = tracker.filterTransactions(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31), groceries);
        assertTrue(filtered.contains(t1));
        assertFalse(filtered.contains(t2));
    }

    @Test
    void testPersistence() {
        String file = "test_finance_data.json";
        tracker.addTransaction(new Transaction("Persist", 50, LocalDate.now(), TransactionType.EXPENSE, groceries));
        tracker.saveToFile(file);
        FinanceTracker loaded = new FinanceTracker();
        loaded.loadFromFile(file);
        assertEquals(1, loaded.getTransactions().size());
        assertEquals("Persist", loaded.getTransactions().get(0).getDescription());
        // Clean up
        new java.io.File(file).delete();
    }
} 