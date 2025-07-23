// JavaFX update: Comprehensive UI with all missing features
package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.time.Month;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainApp extends Application {
    // JavaFX update: Enhanced UI fields
    private FinanceTracker tracker = new FinanceTracker();
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private ObservableList<SpendingLimit> spendingLimitList = FXCollections.observableArrayList(); // JavaFX update: Add spending limits list
    private TableView<Transaction> table = new TableView<>();
    private final String DATA_FILE = "finance_data.json";
    
    // JavaFX update: UI components for balance and summary
    private Label balanceLabel;
    private Label summaryLabel;
    private ComboBox<Integer> yearFilter; // JavaFX update: Separate year filter
    private ComboBox<Month> monthFilter; // JavaFX update: Separate month filter
    private ComboBox<Category> categoryFilter;
    private DatePicker startDateFilter;
    private DatePicker endDateFilter;
    private TextArea breakdownArea; // JavaFX update: Add reference to breakdown area
    private TableView<CategorySummary> summaryTable; // JavaFX update: Add reference to summary table

    @Override
    public void start(Stage primaryStage) {
        // JavaFX update: Enhanced table setup with color coding
        setupTransactionTable();
        
        // JavaFX update: Create main layout with tabs
        TabPane tabPane = new TabPane();
        
        // JavaFX update: Transactions tab
        Tab transactionsTab = new Tab("Transactions", createTransactionsTab());
        transactionsTab.setClosable(false);
        
        // JavaFX update: Categories tab
        Tab categoriesTab = new Tab("Categories", createCategoriesTab());
        categoriesTab.setClosable(false);
        
        // JavaFX update: Summary tab
        Tab summaryTab = new Tab("Summary", createSummaryTab());
        summaryTab.setClosable(false);
        
        tabPane.getTabs().addAll(transactionsTab, categoriesTab, summaryTab);
        
        // JavaFX update: Balance display at top
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        
        // JavaFX update: Always visible balance
        balanceLabel = new Label("Current Balance: $0.00");
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // JavaFX update: Warning label for spending limits
        Label warningLabel = new Label();
        warningLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        warningLabel.setTextFill(Color.RED);
        warningLabel.setVisible(false);
        
        updateBalanceDisplay();
        
        root.getChildren().addAll(balanceLabel, warningLabel, tabPane);
        
        // JavaFX update: Initial load and setup
        loadData();
        setupFilters();
        
        primaryStage.setTitle("Personal Finance Tracker");
        primaryStage.setScene(new Scene(root, 1200, 700));
        
        // JavaFX update: Add close confirmation with save option
        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save Before Exit");
            alert.setHeaderText("Do you want to save your data before closing?");
            alert.setContentText("Your changes will be lost if you don't save.");
            
            ButtonType saveButton = new ButtonType("Save & Exit");
            ButtonType exitButton = new ButtonType("Exit Without Saving");
            ButtonType cancelButton = new ButtonType("Cancel");
            
            alert.getButtonTypes().setAll(saveButton, exitButton, cancelButton);
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == saveButton) {
                    // Save and exit
                    tracker.saveToFile(DATA_FILE);
                    showInfo("Data saved successfully!");
                    // Allow the window to close
                } else if (result.get() == exitButton) {
                    // Exit without saving
                    // Allow the window to close
                } else {
                    // Cancel - prevent the window from closing
                    event.consume();
                }
            } else {
                // User closed the dialog - prevent the window from closing
                event.consume();
            }
        });
        
        primaryStage.show();
    }

    // JavaFX update: Setup transaction table with color coding
    private void setupTransactionTable() {
        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDescription()));
        descCol.setPrefWidth(200);
        
        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", cell.getValue().getAmount())));
        amountCol.setCellFactory(col -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTextFill(Color.BLACK);
                } else {
                    setText(item);
                    Transaction t = getTableView().getItems().get(getIndex());
                    if (t.getType() == TransactionType.INCOME) {
                        setTextFill(Color.GREEN);
                    } else {
                        setTextFill(Color.RED);
                    }
                }
            }
        });
        amountCol.setPrefWidth(100);
        
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDate().toString()));
        dateCol.setPrefWidth(100);
        
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getType().toString()));
        typeCol.setPrefWidth(80);
        
        TableColumn<Transaction, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getCategory() != null ? cell.getValue().getCategory().getName() : ""));
        catCol.setPrefWidth(120);
        
        table.getColumns().addAll(descCol, amountCol, dateCol, typeCol, catCol);
        table.setItems(transactionList);
        
        // JavaFX update: Add double-click to edit transactions
        table.setRowFactory(tv -> {
            TableRow<Transaction> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Transaction transaction = row.getItem();
                    showEditTransactionDialog(transaction);
                }
            });
            return row;
        });
    }

    // JavaFX update: Create transactions tab
    private VBox createTransactionsTab() {
        VBox tab = new VBox(10);
        tab.setPadding(new Insets(10));
        
        // JavaFX update: Input controls with validation
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(5);
        
        TextField descField = new TextField();
        descField.setPromptText("Description");
        descField.setPrefWidth(200);
        
        TextField amountField = new TextField();
        amountField.setPromptText("Amount (e.g., 25.50)");
        amountField.setPrefWidth(120);
        
        DatePicker datePicker = new DatePicker(LocalDate.now());
        
        // JavaFX update: Remove transaction type selection - it's determined by category
        // ComboBox<TransactionType> typeBox = new ComboBox<>(FXCollections.observableArrayList(TransactionType.values()));
        // typeBox.getSelectionModel().selectFirst();
        
        ComboBox<Category> catBox = new ComboBox<>(categoryList);
        // JavaFX update: Fix category dropdown display in transaction creation
        catBox.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Category");
                } else {
                    setText(item.getName());
                }
            }
        });
        
        catBox.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Category");
                } else {
                    setText(item.getName());
                }
            }
        });
        
        Button addBtn = new Button("Add Transaction");
        addBtn.setOnAction(e -> {
            try {
                // JavaFX update: Input validation
                String desc = descField.getText().trim();
                if (desc.isEmpty()) {
                    showAlert("Description cannot be empty");
                    return;
                }
                
                double amt = Double.parseDouble(amountField.getText());
                if (amt <= 0) {
                    showAlert("Amount must be positive");
                    return;
                }
                
                LocalDate date = datePicker.getValue();
                Category cat = catBox.getValue();
                
                if (cat == null) {
                    showAlert("Please select a category");
                    return;
                }
                
                // JavaFX update: Transaction type is automatically determined by category type
                TransactionType type = TransactionType.valueOf(cat.getType().name());
                Transaction t = new Transaction(desc, amt, date, type, cat);
                
                // JavaFX update: Check if this transaction will exceed spending limit
                if (willExceedSpendingLimit(t)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Spending Limit Warning");
                    alert.setHeaderText("This transaction will exceed your spending limit");
                    alert.setContentText("Are you sure you want to add this transaction?");
                    
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        return; // User cancelled
                    }
                }
                
                tracker.addTransaction(t);
                transactionList.add(t);
                
                // JavaFX update: Clear fields and update displays
                descField.clear();
                amountField.clear();
                updateBalanceDisplay();
                updateSummaryDisplay();
                spendingLimitList.setAll(tracker.getSpendingLimits()); // JavaFX update: Refresh spending limits
                tracker.checkSpendingLimits();
                
                // JavaFX update: Check for spending limit warnings
                checkSpendingLimitsOnTransaction(t);
                
            } catch (NumberFormatException ex) {
                showAlert("Invalid amount format. Please enter a valid number (e.g., 25.50)");
            } catch (Exception ex) {
                showAlert("Error: " + ex.getMessage());
            }
        });
        
        inputGrid.add(new Label("Description:"), 0, 0);
        inputGrid.add(descField, 1, 0);
        inputGrid.add(new Label("Amount:"), 0, 1);
        inputGrid.add(amountField, 1, 1);
        inputGrid.add(new Label("Date:"), 0, 2);
        inputGrid.add(datePicker, 1, 2);
        // JavaFX update: Remove type selection row - type is determined by category
        // inputGrid.add(new Label("Type:"), 0, 3);
        // inputGrid.add(typeBox, 1, 3);
        inputGrid.add(new Label("Category:"), 0, 3);
        inputGrid.add(catBox, 1, 3);
        inputGrid.add(addBtn, 1, 4);
        
        // JavaFX update: Action buttons
        HBox actionBox = new HBox(10);
        Button removeBtn = new Button("Remove Selected");
        removeBtn.setOnAction(e -> {
            Transaction t = table.getSelectionModel().getSelectedItem();
            if (t != null) {
                tracker.removeTransaction(t);
                transactionList.remove(t);
                updateBalanceDisplay();
                updateSummaryDisplay();
                spendingLimitList.setAll(tracker.getSpendingLimits()); // JavaFX update: Refresh spending limits
                tracker.checkSpendingLimits();
            }
        });
        
        Button editBtn = new Button("Edit Selected");
        editBtn.setOnAction(e -> {
            Transaction t = table.getSelectionModel().getSelectedItem();
            if (t != null) {
                showEditTransactionDialog(t);
            }
        });
        
        Button saveBtn = new Button("Save Data");
        saveBtn.setOnAction(e -> {
            tracker.saveToFile(DATA_FILE);
            showInfo("Data saved successfully!");
        });
        
        Button loadBtn = new Button("Load Data");
        loadBtn.setOnAction(e -> {
            loadData();
            showInfo("Data loaded successfully!");
        });
        
        actionBox.getChildren().addAll(removeBtn, editBtn, saveBtn, loadBtn);
        
        // JavaFX update: Filtering controls
        HBox filterBox = new HBox(10);
        filterBox.setPadding(new Insets(10, 0, 0, 0));
        
        startDateFilter = new DatePicker();
        endDateFilter = new DatePicker();
        
        categoryFilter = new ComboBox<>();
        categoryFilter.getItems().add(null); // Allow "All categories"
        categoryFilter.getItems().addAll(categoryList);
        categoryFilter.getSelectionModel().selectFirst();
        
        Button filterBtn = new Button("Apply Filters");
        filterBtn.setOnAction(e -> applyFilters());
        
        Button clearFilterBtn = new Button("Clear Filters");
        clearFilterBtn.setOnAction(e -> {
            startDateFilter.setValue(null);
            endDateFilter.setValue(null);
            categoryFilter.getSelectionModel().selectFirst();
            transactionList.setAll(tracker.getTransactions());
        });
        
        filterBox.getChildren().addAll(
            new Label("Start Date:"), startDateFilter,
            new Label("End Date:"), endDateFilter,
            new Label("Category:"), categoryFilter,
            filterBtn, clearFilterBtn
        );
        
        tab.getChildren().addAll(inputGrid, actionBox, filterBox, table);
        return tab;
    }

    // JavaFX update: Create categories tab
    private VBox createCategoriesTab() {
        VBox tab = new VBox(10);
        tab.setPadding(new Insets(10));
        
        // JavaFX update: Category management
        GridPane categoryGrid = new GridPane();
        categoryGrid.setHgap(10);
        categoryGrid.setVgap(5);
        
        TextField catNameField = new TextField();
        catNameField.setPromptText("Category Name");
        catNameField.setPrefWidth(200);
        
        ComboBox<CategoryType> catTypeBox = new ComboBox<>(FXCollections.observableArrayList(CategoryType.values()));
        catTypeBox.getSelectionModel().selectFirst();
        
        Button addCatBtn = new Button("Add Category");
        addCatBtn.setOnAction(e -> {
            String name = catNameField.getText().trim();
            if (name.isEmpty()) {
                showAlert("Category name cannot be empty");
                return;
            }
            
            CategoryType type = catTypeBox.getValue();
            Category newCat = new Category(name, type);
            tracker.addCategory(newCat);
            categoryList.add(newCat);
            catNameField.clear();
            updateCategoryFilters();
        });
        
        categoryGrid.add(new Label("Name:"), 0, 0);
        categoryGrid.add(catNameField, 1, 0);
        categoryGrid.add(new Label("Type:"), 0, 1);
        categoryGrid.add(catTypeBox, 1, 1);
        categoryGrid.add(addCatBtn, 1, 2);
        
        // JavaFX update: Category list with actions - Fix display issue
        ListView<Category> categoryListView = new ListView<>(categoryList);
        categoryListView.setPrefHeight(200);
        categoryListView.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getType() + ")");
                }
            }
        });
        
        HBox catActionBox = new HBox(10);
        Button renameCatBtn = new Button("Rename Category");
        renameCatBtn.setOnAction(e -> {
            Category selected = categoryListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showRenameDialog(selected);
            }
        });
        
        Button deleteCatBtn = new Button("Delete Category");
        deleteCatBtn.setOnAction(e -> {
            Category selected = categoryListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showDeleteDialog(selected);
            }
        });
        
        catActionBox.getChildren().addAll(renameCatBtn, deleteCatBtn);
        
        // JavaFX update: Spending limits section
        VBox limitsBox = new VBox(10);
        limitsBox.setPadding(new Insets(10, 0, 0, 0));
        limitsBox.getChildren().add(new Label("Spending Limits:"));
        
        ListView<SpendingLimit> limitsListView = new ListView<>(spendingLimitList); // JavaFX update: Connect to observable list
        limitsListView.setPrefHeight(150);
        limitsListView.setCellFactory(param -> new ListCell<SpendingLimit>() {
            @Override
            protected void updateItem(SpendingLimit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    double currentSpent = item.getCurrentSpent();
                    double monthlyLimit = item.getMonthlyLimit();
                    double percentage = (currentSpent / monthlyLimit) * 100;
                    setText(String.format("%s: $%.2f / $%.2f (%.1f%%)",
                        item.getCategory().getName(),
                        currentSpent,
                        monthlyLimit,
                        percentage));
                    // Only apply custom background if not selected
                    if (isSelected()) {
                        setStyle("");
                    } else if (percentage > 100) {
                        setStyle("-fx-text-fill: #d32f2f; -fx-background-color: #ffebee; -fx-font-weight: bold;");
                    } else if (percentage >= 80) {
                        setStyle("-fx-text-fill: #f57c00; -fx-background-color: #fff3e0; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });
        
        Button addLimitBtn = new Button("Add Spending Limit");
        addLimitBtn.setOnAction(e -> {
            showAddLimitDialog();
            // JavaFX update: Refresh spending limits list after adding
            spendingLimitList.setAll(tracker.getSpendingLimits());
        });
        
        Button removeLimitBtn = new Button("Remove Selected Limit");
        removeLimitBtn.setOnAction(e -> {
            SpendingLimit selected = limitsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showRemoveLimitDialog(selected);
            }
        });
        
        limitsBox.getChildren().addAll(limitsListView, addLimitBtn, removeLimitBtn);
        
        tab.getChildren().addAll(categoryGrid, categoryListView, catActionBox, limitsBox);
        return tab;
    }

    // JavaFX update: Create summary tab
    private VBox createSummaryTab() {
        VBox tab = new VBox(10);
        tab.setPadding(new Insets(10));
        
        // JavaFX update: Month filter for summary with separate year/month dropdowns
        HBox monthFilterBox = new HBox(10);
        yearFilter = new ComboBox<>();
        monthFilter = new ComboBox<>();
        
        // JavaFX update: Populate year and month filters
        populateYearMonthFilters();
        
        Button updateSummaryBtn = new Button("Update Summary");
        updateSummaryBtn.setOnAction(e -> updateSummaryDisplay());
        
        monthFilterBox.getChildren().addAll(new Label("Year:"), yearFilter, new Label("Month:"), monthFilter, updateSummaryBtn);
        
        // JavaFX update: Summary display with better formatting
        summaryLabel = new Label();
        summaryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        summaryLabel.setWrapText(true);
        
        // JavaFX update: Category breakdown table instead of text area
        TableView<CategorySummary> summaryTable = new TableView<>();
        summaryTable.setPrefHeight(300);
        summaryTable.setEditable(false);
        
        // JavaFX update: Table columns for category breakdown
        TableColumn<CategorySummary, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryCol.setPrefWidth(150);
        
        TableColumn<CategorySummary, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(80);
        
        TableColumn<CategorySummary, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setCellFactory(col -> new TableCell<CategorySummary, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    CategorySummary summary = getTableView().getItems().get(getIndex());
                    if (summary.getType().equals("INCOME")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });
        totalCol.setPrefWidth(100);
        
        TableColumn<CategorySummary, String> limitCol = new TableColumn<>("Spending Limit");
        limitCol.setCellValueFactory(new PropertyValueFactory<>("spendingLimit"));
        limitCol.setPrefWidth(120);
        
        TableColumn<CategorySummary, String> usageCol = new TableColumn<>("Usage %");
        usageCol.setCellValueFactory(new PropertyValueFactory<>("usagePercentage"));
        usageCol.setCellFactory(col -> new TableCell<CategorySummary, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    CategorySummary summary = getTableView().getItems().get(getIndex());
                    if (summary.getUsagePercentage() != null && !summary.getUsagePercentage().equals("N/A")) {
                        double percentage = Double.parseDouble(summary.getUsagePercentage().replace("%", ""));
                        if (percentage > 100) {
                            setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                        } else if (percentage >= 80) {
                            setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-text-fill: black;");
                        }
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });
        usageCol.setPrefWidth(80);
        
        summaryTable.getColumns().addAll(categoryCol, typeCol, totalCol, limitCol, usageCol);
        
        // JavaFX update: Store reference to summary table
        this.summaryTable = summaryTable;
        
        tab.getChildren().addAll(monthFilterBox, summaryLabel, new Label("Category Breakdown:"), summaryTable);
        return tab;
    }
    
    // JavaFX update: Populate year and month filters
    private void populateYearMonthFilters() {
        // Populate years (current year ± 10 years)
        int currentYear = YearMonth.now().getYear();
        List<Integer> years = new ArrayList<>();
        for (int year = currentYear - 10; year <= currentYear + 5; year++) {
            years.add(year);
        }
        yearFilter.getItems().addAll(years);
        yearFilter.getSelectionModel().select(Integer.valueOf(currentYear));
        
        // Populate months
        monthFilter.getItems().addAll(Month.values());
        monthFilter.getSelectionModel().select(YearMonth.now().getMonth());
    }
    
    private void updateSummaryDisplay() {
        YearMonth selectedMonth = YearMonth.of(yearFilter.getValue(), monthFilter.getValue());
        if (selectedMonth != null) {
            FinanceSummary summary = tracker.getMonthlySummary(selectedMonth);
            
            // JavaFX update: Format summary with colors
            StringBuilder summaryText = new StringBuilder();
            summaryText.append("Month: ").append(selectedMonth.toString()).append("\n\n");
            summaryText.append("Total Income: $").append(String.format("%.2f", summary.getTotalIncome())).append("\n");
            summaryText.append("Total Expenses: $").append(String.format("%.2f", summary.getTotalExpenses())).append("\n");
            summaryText.append("Net Balance: $").append(String.format("%.2f", summary.getNetBalance()));
            
            summaryLabel.setText(summaryText.toString());
            
            // JavaFX update: Set colors for income and expenses
            if (summary.getTotalIncome() > 0) {
                summaryLabel.setStyle("-fx-text-fill: green;");
            } else if (summary.getTotalExpenses() > 0) {
                summaryLabel.setStyle("-fx-text-fill: red;");
            } else {
                summaryLabel.setStyle("-fx-text-fill: black;");
            }
            
            // JavaFX update: Populate summary table
            populateSummaryTable(selectedMonth);
        }
    }
    
    // JavaFX update: Populate summary table with category data
    private void populateSummaryTable(YearMonth month) {
        ObservableList<CategorySummary> tableData = FXCollections.observableArrayList();
        
        // Get transactions for the selected month
        List<Transaction> monthTransactions = tracker.filterTransactions(
            month.atDay(1), 
            month.atEndOfMonth(), 
            null
        );
        
        // Group transactions by category
        Map<Category, List<Transaction>> categoryGroups = new HashMap<>();
        for (Transaction t : monthTransactions) {
            Category cat = t.getCategory();
            if (cat != null) {
                categoryGroups.computeIfAbsent(cat, k -> new ArrayList<>()).add(t);
            }
        }
        
        // Create table rows for each category
        for (Category category : categoryGroups.keySet()) {
            List<Transaction> transactions = categoryGroups.get(category);
            double total = transactions.stream().mapToDouble(Transaction::getAmount).sum();
            
            // Find spending limit for this category
            SpendingLimit limit = tracker.getSpendingLimits().stream()
                .filter(sl -> sl.getCategory().equals(category))
                .findFirst()
                .orElse(null);
            
            String spendingLimitText = limit != null ? String.format("$%.2f", limit.getMonthlyLimit()) : "N/A";
            String usagePercentageText = "N/A";
            
            if (limit != null && limit.getMonthlyLimit() > 0) {
                double percentage = (total / limit.getMonthlyLimit()) * 100;
                usagePercentageText = String.format("%.1f%%", percentage);
            }
            
            CategorySummary summary = new CategorySummary(
                category.getName(),
                category.getType().toString(),
                String.format("$%.2f", total),
                spendingLimitText,
                usagePercentageText
            );
            
            tableData.add(summary);
        }
        
        // Update the table
        summaryTable.setItems(tableData);
    }
    
    // JavaFX update: Generate detailed category breakdown
    private void generateCategoryBreakdown(YearMonth month) {
        // This method is no longer needed as the summary table handles display
        // Keeping it for now in case it's called elsewhere or for future use
    }
    
    // JavaFX update: Helper methods
    private void loadData() {
        tracker.loadFromFile(DATA_FILE);
        transactionList.setAll(tracker.getTransactions());
        categoryList.setAll(tracker.getCategories());
        spendingLimitList.setAll(tracker.getSpendingLimits()); // Load spending limits
        updateBalanceDisplay();
        updateSummaryDisplay();
    }
    
    private void setupFilters() {
        updateCategoryFilters();
    }
    
    private void updateCategoryFilters() {
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add(null);
        categoryFilter.getItems().addAll(categoryList);
        categoryFilter.getSelectionModel().selectFirst();
        
        // JavaFX update: Fix category filter display to show names instead of object references
        categoryFilter.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("All Categories");
                } else {
                    setText(item.getName());
                }
            }
        });
        
        categoryFilter.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("All Categories");
                } else {
                    setText(item.getName());
                }
            }
        });
    }
    
    private void updateBalanceDisplay() {
        double balance = tracker.getCurrentBalance();
        String color = balance >= 0 ? "green" : "red";
        balanceLabel.setText(String.format("Current Balance: $%.2f", balance));
        balanceLabel.setStyle(String.format("-fx-text-fill: %s;", color));

        // JavaFX update: Check for spending limit warnings
        tracker.checkSpendingLimits(); // Update current spent amounts
        spendingLimitList.setAll(tracker.getSpendingLimits()); // JavaFX update: Refresh spending limits
        
        // JavaFX update: Only show warnings when adding transactions, not on startup
        // Removed automatic warning popup from balance display
    }
    
    // JavaFX update: Check spending limits when adding transactions
    private void checkSpendingLimitsOnTransaction(Transaction transaction) {
        tracker.checkSpendingLimits(); // Update current spent amounts
        
        // JavaFX update: Only check the category of the current transaction
        if (transaction.getCategory() == null) {
            return;
        }
        
        for (SpendingLimit limit : tracker.getSpendingLimits()) {
            if (limit.getCategory().equals(transaction.getCategory())) {
                double currentSpent = limit.getCurrentSpent();
                double monthlyLimit = limit.getMonthlyLimit();
                
                if (monthlyLimit > 0 && currentSpent > 0) {
                    double percentage = (currentSpent / monthlyLimit) * 100;
                    
                    if (percentage > 100) {
                        // Show a popup alert for limit exceeded warning
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Spending Limit Warning");
                        alert.setHeaderText("Spending Limit Exceeded");
                        alert.setContentText("LIMIT EXCEEDED: " + limit.getCategory().getName() +
                                          " ($" + String.format("%.2f", currentSpent) +
                                          "/$" + String.format("%.2f", monthlyLimit) + ")");
                        alert.showAndWait();
                    } else if (percentage >= 80) {
                        // Show a popup alert for approaching limit warning
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Spending Limit Warning");
                        alert.setHeaderText("Approaching Spending Limit");
                        alert.setContentText("APPROACHING LIMIT: " + limit.getCategory().getName() +
                                          " ($" + String.format("%.2f", currentSpent) +
                                          "/$" + String.format("%.2f", monthlyLimit) + ")");
                        alert.showAndWait();
                    }
                }
                break; // Only check the current transaction's category
            }
        }
    }
    
    // JavaFX update: Check if adding a transaction will exceed spending limit
    private boolean willExceedSpendingLimit(Transaction transaction) {
        if (transaction.getType() != TransactionType.EXPENSE || transaction.getCategory() == null) {
            return false;
        }
        
        for (SpendingLimit limit : tracker.getSpendingLimits()) {
            if (limit.getCategory().equals(transaction.getCategory())) {
                double currentSpent = limit.getCurrentSpent();
                double monthlyLimit = limit.getMonthlyLimit();
                double newTotal = currentSpent + transaction.getAmount();
                
                if (monthlyLimit > 0 && newTotal > monthlyLimit) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // JavaFX update: Fix filtering null pointer issue
    private void applyFilters() {
        LocalDate start = startDateFilter.getValue();
        LocalDate end = endDateFilter.getValue();
        Category category = categoryFilter.getValue();
        
        // JavaFX update: Add null checks for date filtering
        if (start != null && end != null && start.isAfter(end)) {
            showAlert("Start date must be before end date");
            return;
        }
        
        List<Transaction> filtered = tracker.filterTransactions(start, end, category);
        transactionList.setAll(filtered);
    }
    
    // JavaFX update: Add transaction editing dialog
    private void showEditTransactionDialog(Transaction transaction) {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Edit Transaction");
        dialog.setHeaderText("Edit transaction: " + transaction.getDescription());
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField descField = new TextField(transaction.getDescription());
        TextField amountField = new TextField(String.valueOf(transaction.getAmount()));
        DatePicker datePicker = new DatePicker(transaction.getDate());
        // JavaFX update: Remove transaction type selection - it's determined by category
        // ComboBox<TransactionType> typeBox = new ComboBox<>(FXCollections.observableArrayList(TransactionType.values()));
        // typeBox.setValue(transaction.getType());
        ComboBox<Category> catBox = new ComboBox<>(categoryList);
        catBox.setValue(transaction.getCategory());
        // JavaFX update: Fix category dropdown display in edit transaction dialog
        catBox.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Category");
                } else {
                    setText(item.getName());
                }
            }
        });
        
        catBox.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Category");
                } else {
                    setText(item.getName());
                }
            }
        });
        
        grid.add(new Label("Description:"), 0, 0);
        grid.add(descField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        // JavaFX update: Remove type selection row - type is determined by category
        // grid.add(new Label("Type:"), 0, 3);
        // grid.add(typeBox, 1, 3);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(catBox, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String desc = descField.getText().trim();
                    if (desc.isEmpty()) {
                        showAlert("Description cannot be empty");
                        return null;
                    }
                    
                    double amount = Double.parseDouble(amountField.getText());
                    if (amount <= 0) {
                        showAlert("Amount must be positive");
                        return null;
                    }
                    
                    LocalDate date = datePicker.getValue();
                    // JavaFX update: Transaction type is automatically determined by category
                    TransactionType type = TransactionType.valueOf(catBox.getValue().getType().name());
                    Category category = catBox.getValue();
                    
                    // JavaFX update: Update the transaction
                    transaction.setDescription(desc);
                    transaction.setAmount(amount);
                    transaction.setDate(date);
                    transaction.setType(type);
                    transaction.setCategory(category);
                    
                    // JavaFX update: Refresh displays
                    table.refresh();
                    updateBalanceDisplay();
                    updateSummaryDisplay();
                    spendingLimitList.setAll(tracker.getSpendingLimits()); // JavaFX update: Refresh spending limits
                    
                    return transaction;
                } catch (NumberFormatException e) {
                    showAlert("Invalid amount format");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void showRenameDialog(Category category) {
        TextInputDialog dialog = new TextInputDialog(category.getName());
        dialog.setTitle("Rename Category");
        dialog.setHeaderText("Enter new name for category: " + category.getName());
        dialog.setContentText("New name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                tracker.renameCategory(category, newName.trim());
                categoryList.setAll(tracker.getCategories());
                updateCategoryFilters();
                
                // JavaFX update: Force refresh of transaction table to show updated category names
                List<Transaction> currentTransactions = new ArrayList<>(transactionList);
                transactionList.clear();
                transactionList.addAll(currentTransactions);
                table.refresh();
            }
        });
    }
    
    private void showDeleteDialog(Category category) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Category");
        alert.setHeaderText("Delete category: " + category.getName());
        alert.setContentText("This will delete the category. Transactions using this category will need to be reassigned to a different category by editing them. Continue?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            tracker.deleteCategory(category);
            categoryList.setAll(tracker.getCategories());
            transactionList.setAll(tracker.getTransactions());
            spendingLimitList.setAll(tracker.getSpendingLimits()); // JavaFX update: Refresh spending limits
            updateCategoryFilters();
            updateBalanceDisplay();
            updateSummaryDisplay();
        }
    }
    
    // JavaFX update: Add spending limit dialog
    private void showAddLimitDialog() {
        Dialog<SpendingLimit> dialog = new Dialog<>();
        dialog.setTitle("Add Spending Limit");
        dialog.setHeaderText("Set a monthly spending limit for a category");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // JavaFX update: Filter to only show expense categories
        ComboBox<Category> catBox = new ComboBox<>();
        catBox.getItems().addAll(tracker.getCategories().stream()
            .filter(cat -> cat.getType() == CategoryType.EXPENSE)
            .collect(java.util.stream.Collectors.toList()));
        
        catBox.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Expense Category");
                } else {
                    setText(item.getName());
                }
            }
        });
        
        catBox.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Expense Category");
                } else {
                    setText(item.getName());
                }
            }
        });
        
        TextField limitField = new TextField();
        limitField.setPromptText("Monthly limit (e.g., 300.00)");
        
        grid.add(new Label("Category:"), 0, 0);
        grid.add(catBox, 1, 0);
        grid.add(new Label("Monthly Limit:"), 0, 1);
        grid.add(limitField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Category category = catBox.getValue();
                    if (category == null) {
                        showAlert("Please select a category");
                        return null;
                    }
                    
                    double limit = Double.parseDouble(limitField.getText());
                    if (limit <= 0) {
                        showAlert("Limit must be positive");
                        return null;
                    }
                    
                    SpendingLimit spendingLimit = new SpendingLimit(category, limit);
                    tracker.setSpendingLimit(category, limit);
                    return spendingLimit;
                } catch (NumberFormatException e) {
                    showAlert("Invalid limit format");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }

    private void showRemoveLimitDialog(SpendingLimit limit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Spending Limit");
        alert.setHeaderText("Remove spending limit for category: " + limit.getCategory().getName());
        alert.setContentText("Are you sure you want to remove this spending limit? This will remove the spending limit for this category.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            tracker.removeSpendingLimit(limit.getCategory());
            spendingLimitList.remove(limit);
            updateCategoryFilters(); // Refresh category filters to remove the removed category
            updateBalanceDisplay();
            updateSummaryDisplay();
        }
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 