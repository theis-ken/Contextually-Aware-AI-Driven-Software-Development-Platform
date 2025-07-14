1. Spending Limit Update on Expense
Add a category with a spending limit.
Add an expense transaction in that category.
Assert that the spending limit’s currentSpent is updated accordingly.

2. Persistence and Data Integrity
Add multiple categories and transactions (with links).
Save to file.
Reload from file.
Assert that all transactions, categories, and spending limits are restored and linked as before.

3. Category Deletion Propagation
Add a category, transactions, and a spending limit.
Delete the category.
Assert that:
The category is removed from the tracker.
All transactions referencing the category have it set to null.
The spending limit for that category is removed.

4. Monthly Summary Accuracy
Add income and expense transactions in different months and categories.
Assert that getMonthlySummary returns correct totals and breakdowns for each month.

5. Transaction Filtering
Add transactions with various dates and categories.
Use filterTransactions to select by date range and category.
Assert that only the correct transactions are returned.

6.Spending Limit Warnings
Add transactions to approach and exceed a category’s spending limit.
Capture output or use a callback to assert that warnings are triggered at 80\% and 100\%.

7.Category Rename Propagation
Rename a category.
Assert that all transactions and spending limits referencing the category reflect the new name.

result:

Running com.example.FinanceTrackerIntegrationTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.074 s -- in com.example.FinanceTrackerIntegrationTest