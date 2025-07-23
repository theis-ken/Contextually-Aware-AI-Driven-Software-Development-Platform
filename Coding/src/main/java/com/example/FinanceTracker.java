// Prompt 3: Moved to com.example package for Maven structure
package com.example;

//Prompt 1: Core logic

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
//Prompt 2: Data model management (Json)
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
// Prompt 5: LocalDate TypeAdapter for Gson
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public class FinanceTracker {
    // Prompt 1: Data fields
    private List<Transaction> transactions = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<SpendingLimit> spendingLimits = new ArrayList<>();

    // Prompt 1: Core methods
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        updateSpendingLimit(transaction, true);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
        updateSpendingLimit(transaction, false);
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void renameCategory(Category category, String newName) {
        category.setName(newName);
    }

    public void deleteCategory(Category category) {
        categories.remove(category);
        for (Transaction t : transactions) {
            if (t.getCategory().equals(category)) {
                t.setCategory(null);
            }
        }
        spendingLimits.removeIf(sl -> sl.getCategory().equals(category));
    }

    public void setSpendingLimit(Category category, double limit) {
        // Remove existing limit for this category if it exists
        spendingLimits.removeIf(sl -> sl.getCategory().equals(category));
        
        // Add new limit
        if (limit > 0) {
            spendingLimits.add(new SpendingLimit(category, limit));
        }
    }
    
    // JavaFX update: Add method to remove spending limit
    public void removeSpendingLimit(Category category) {
        spendingLimits.removeIf(sl -> sl.getCategory().equals(category));
    }

    public FinanceSummary getMonthlySummary(YearMonth month) {
        double totalIncome = 0;
        double totalExpenses = 0;
        Map<Category, Double> categoryBreakdown = new HashMap<>();
        for (Transaction t : transactions) {
            if (YearMonth.from(t.getDate()).equals(month)) {
                double amt = t.getAmount();
                if (t.getType() == TransactionType.INCOME) {
                    totalIncome += amt;
                } else {
                    totalExpenses += amt;
                }
                categoryBreakdown.put(t.getCategory(),
                    categoryBreakdown.getOrDefault(t.getCategory(), 0.0) + amt);
            }
        }
        double netBalance = totalIncome - totalExpenses;
        return new FinanceSummary(totalIncome, totalExpenses, netBalance, categoryBreakdown);
    }

    public List<Transaction> filterTransactions(LocalDate start, LocalDate end, Category category) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : transactions) {
            // JavaFX update: Fix null pointer issues with date filtering
            boolean inRange = true;
            if (start != null && end != null) {
                inRange = (t.getDate().isEqual(start) || t.getDate().isAfter(start)) &&
                          (t.getDate().isEqual(end) || t.getDate().isBefore(end));
            } else if (start != null) {
                inRange = t.getDate().isEqual(start) || t.getDate().isAfter(start);
            } else if (end != null) {
                inRange = t.getDate().isEqual(end) || t.getDate().isBefore(end);
            }
            
            // JavaFX update: Fix null pointer issues with category filtering
            boolean inCategory = true;
            if (category != null) {
                inCategory = t.getCategory() != null && t.getCategory().equals(category);
            }
            
            if (inRange && inCategory) {
                filtered.add(t);
            }
        }
        return filtered;
    }

    public double getCurrentBalance() {
        double income = 0, expense = 0;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) income += t.getAmount();
            else expense += t.getAmount();
        }
        return income - expense;
    }

    public void checkSpendingLimits() {
        Map<Category, Double> monthlyExpenses = new HashMap<>();
        YearMonth currentMonth = YearMonth.now();
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.EXPENSE && YearMonth.from(t.getDate()).equals(currentMonth)) {
                Category cat = t.getCategory();
                monthlyExpenses.put(cat, monthlyExpenses.getOrDefault(cat, 0.0) + t.getAmount());
            }
        }
        for (SpendingLimit sl : spendingLimits) {
            double spent = monthlyExpenses.getOrDefault(sl.getCategory(), 0.0);
            sl.setCurrentSpent(spent);
            if (sl.getMonthlyLimit() > 0) {
                if (spent >= sl.getMonthlyLimit()) {
                    System.out.println("Warning: Spending limit exceeded for category: " + sl.getCategory().getName());
                } else if (spent >= 0.8 * sl.getMonthlyLimit()) {
                    System.out.println("Warning: 80% of spending limit reached for category: " + sl.getCategory().getName());
                }
            }
        }
    }

    private void updateSpendingLimit(Transaction transaction, boolean add) {
        if (transaction.getType() == TransactionType.EXPENSE) {
            for (SpendingLimit sl : spendingLimits) {
                if (sl.getCategory().equals(transaction.getCategory())) {
                    double current = sl.getCurrentSpent();
                    if (add) {
                        sl.setCurrentSpent(current + transaction.getAmount());
                    } else {
                        sl.setCurrentSpent(current - transaction.getAmount());
                    }
                }
            }
        }
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<SpendingLimit> getSpendingLimits() {
        return spendingLimits;
    }

    // Prompt 2: Persistence methods using Gson
    public void saveToFile(String filePath) {
        // Prompt 5: Register LocalDate adapter
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting().create();
        DataWrapper wrapper = new DataWrapper(transactions, categories, spendingLimits);
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(wrapper, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String filePath) {
        // Prompt 5: Register LocalDate adapter
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
        try (Reader reader = new FileReader(filePath)) {
            DataWrapper wrapper = gson.fromJson(reader, DataWrapper.class);
            if (wrapper != null) {
                this.transactions = wrapper.transactions != null ? wrapper.transactions : new ArrayList<>();
                this.categories = wrapper.categories != null ? wrapper.categories : new ArrayList<>();
                this.spendingLimits = wrapper.spendingLimits != null ? wrapper.spendingLimits : new ArrayList<>();
                
                // JavaFX update: Link categories in transactions to the same objects in categories list
                linkCategoriesInTransactions();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // JavaFX update: Link categories in transactions to the same objects in categories list
    private void linkCategoriesInTransactions() {
        for (Transaction transaction : transactions) {
            if (transaction.getCategory() != null) {
                // Find the matching category in the categories list
                for (Category category : categories) {
                    if (category.getName().equals(transaction.getCategory().getName()) && 
                        category.getType() == transaction.getCategory().getType()) {
                        // Replace the transaction's category with the one from the categories list
                        transaction.setCategory(category);
                        break;
                    }
                }
            }
        }
        
        // Also link categories in spending limits
        for (SpendingLimit limit : spendingLimits) {
            if (limit.getCategory() != null) {
                for (Category category : categories) {
                    if (category.getName().equals(limit.getCategory().getName()) && 
                        category.getType() == limit.getCategory().getType()) {
                        limit.setCategory(category);
                        break;
                    }
                }
            }
        }
    }

    // Prompt 2: Helper class for JSON serialization
    private static class DataWrapper {
        List<Transaction> transactions;
        List<Category> categories;
        List<SpendingLimit> spendingLimits;
        DataWrapper(List<Transaction> transactions, List<Category> categories, List<SpendingLimit> spendingLimits) {
            this.transactions = transactions;
            this.categories = categories;
            this.spendingLimits = spendingLimits;
        }
    }

    // Prompt 5: LocalDate TypeAdapter for Gson
    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            out.value(value != null ? value.toString() : null);
        }
        @Override
        public LocalDate read(JsonReader in) throws IOException {
            String s = in.nextString();
            return s != null ? LocalDate.parse(s) : null;
        }
    }
}