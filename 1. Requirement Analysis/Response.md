User Stories: 

1. Record a New Transaction
As a user, I want to record a new transaction by entering a description, amount, date, type (expense/income), and category, so I can track my finances accurately.

2. View Monthly Financial Summary
As a user, I want to see a summary of my finances for a selected month, including total income, total expenses, and remaining balance, so I can understand my financial situation.

3. View Category Breakdown
As a user, I want to see a breakdown of my finances by category for a selected month, so I can analyze where my money goes.

4. Manage Categories
As a user, I want to create, rename, and delete custom categories for expenses and income, so I can organize my transactions in a way that makes sense to me.

5. Filter Transactions
As a user, I want to filter my transactions by date range and/or category, so I can analyze specific spending or earning patterns.

6. Set and Monitor Category Spending Limits
As a user, I want to set a monthly spending limit for a category and receive a warning when I approach the limit, so I can control my budget.

7. View Current Balance
As a user, I want to always see my current balance (total income minus total expenses), which updates automatically with every transaction, so I know how much money I have.


Acceptance Criteria:

1. Record a New Transaction
[ ] User can input description, amount, date, type (expense/income), and select or create a category.
[ ] Transaction is saved and appears in the transaction list.
[ ] Balance updates immediately after adding/removing a transaction.

2. View Monthly Financial Summary
[ ] User can select a month to view.
[ ] Summary displays total income, total expenses, and remaining balance for the selected month.

3. View Category Breakdown
[ ] User can view a breakdown of income and expenses by category for the selected month.
[ ] Each category shows the total amount spent or earned.

4. Manage Categories
[ ] User can create new categories for income or expenses.
[ ] User can rename existing categories.
[ ] User can delete categories (with a prompt to reassign or delete associated transactions).

5. Filter Transactions
[ ] User can filter transactions by date range.
[ ] User can filter transactions by category.
[ ] Filtered results update the transaction list and summary accordingly.

6. Set and Monitor Category Spending Limits
[ ] User can set a monthly spending limit for any expense category.
[ ] System warns the user when expenses in a category reach 80% of the limit.
[ ] System warns the user when the limit is exceeded.

7. View Current Balance
[ ] Current balance is always visible on the main dashboard.
[ ] Balance updates in real-time as transactions are added, edited, or deleted.
Formal Requirements

Functional Requirements

The system shall allow users to create, edit, and delete transactions with the following fields: description, amount, date, type (expense/income), and category.
The system shall allow users to create, rename, and delete custom categories for both income and expenses.
The system shall provide a monthly summary view showing total income, total expenses, and net balance for a selected month.
The system shall provide a category breakdown view for a selected month, displaying totals per category.
The system shall allow users to filter transactions by date range and/or category.
The system shall allow users to set a monthly spending limit for any expense category.
The system shall notify users when their spending in a category reaches 80\% and 100\% of the set limit.
The system shall always display the current balance (total income minus total expenses), updating automatically with any transaction changes.
Non-Functional Requirements
The system shall update all summaries and balances in real-time as data changes.
The system shall provide a user-friendly interface for managing transactions and categories.
The system shall ensure data integrity when categories are renamed or deleted (e.g., prompt for reassignment or deletion of associated transactions).
The system shall be responsive and accessible on both desktop and mobile devices (if applicable).