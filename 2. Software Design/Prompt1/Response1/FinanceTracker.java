package Response1;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class FinanceTracker {
    private List<Transaction> transactions;
    private List<Category> categories;
    private List<SpendingLimit> spendingLimits;

    public void addTransaction(Transaction transaction) { /* ... */ }
    public void removeTransaction(Transaction transaction) { /* ... */ }
    public void addCategory(Category category) { /* ... */ }
    public void renameCategory(Category category, String newName) { /* ... */ }
    public void deleteCategory(Category category) { /* ... */ }
    public void setSpendingLimit(Category category, double limit) { /* ... */ }
    public FinanceSummary getMonthlySummary(YearMonth month) { return null;}
    public List<Transaction> filterTransactions(LocalDate start, LocalDate end, Category category) { return null; }
    public double getCurrentBalance() { return 0; }
    public void checkSpendingLimits() { /* ... */ }
    // ... other methods as needed
}