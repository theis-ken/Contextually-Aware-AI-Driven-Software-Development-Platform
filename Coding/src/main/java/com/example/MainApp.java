// Prompt 4: JavaFX UI for FinanceTracker
package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainApp extends Application {
    // Prompt 4: UI fields
    private FinanceTracker tracker = new FinanceTracker();
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private TableView<Transaction> table = new TableView<>();
    private final String DATA_FILE = "finance_data.json";

    @Override
    public void start(Stage primaryStage) {
        // Prompt 4: Table setup
        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDescription()));
        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getAmount())));
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDate().toString()));
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getType().toString()));
        TableColumn<Transaction, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getCategory() != null ? cell.getValue().getCategory().getName() : ""));
        table.getColumns().addAll(descCol, amountCol, dateCol, typeCol, catCol);
        table.setItems(transactionList);

        // Prompt 4: Controls for adding transactions
        TextField descField = new TextField(); descField.setPromptText("Description");
        TextField amountField = new TextField(); amountField.setPromptText("Amount");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        ComboBox<TransactionType> typeBox = new ComboBox<>(FXCollections.observableArrayList(TransactionType.values()));
        typeBox.getSelectionModel().selectFirst();
        ComboBox<Category> catBox = new ComboBox<>(FXCollections.observableArrayList(tracker.getCategories()));
        Button addBtn = new Button("Add Transaction");
        addBtn.setOnAction(e -> {
            try {
                String desc = descField.getText();
                double amt = Double.parseDouble(amountField.getText());
                LocalDate date = datePicker.getValue();
                TransactionType type = typeBox.getValue();
                Category cat = catBox.getValue();
                Transaction t = new Transaction(desc, amt, date, type, cat);
                tracker.addTransaction(t);
                transactionList.add(t);
                descField.clear(); amountField.clear();
            } catch (Exception ex) {
                showAlert("Invalid input: " + ex.getMessage());
            }
        });

        // Prompt 4: Remove transaction
        Button removeBtn = new Button("Remove Selected");
        removeBtn.setOnAction(e -> {
            Transaction t = table.getSelectionModel().getSelectedItem();
            if (t != null) {
                tracker.removeTransaction(t);
                transactionList.remove(t);
            }
        });

        // Prompt 4: Save/load buttons
        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> tracker.saveToFile(DATA_FILE));
        Button loadBtn = new Button("Load");
        loadBtn.setOnAction(e -> {
            tracker.loadFromFile(DATA_FILE);
            transactionList.setAll(tracker.getTransactions());
            catBox.setItems(FXCollections.observableArrayList(tracker.getCategories()));
        });

        // Prompt 4: Layout
        HBox inputBox = new HBox(5, descField, amountField, datePicker, typeBox, catBox, addBtn);
        HBox actionBox = new HBox(5, removeBtn, saveBtn, loadBtn);
        VBox root = new VBox(10, table, inputBox, actionBox);
        root.setPadding(new Insets(10));

        // Prompt 4: Initial load
        tracker.loadFromFile(DATA_FILE);
        transactionList.setAll(tracker.getTransactions());
        catBox.setItems(FXCollections.observableArrayList(tracker.getCategories()));

        primaryStage.setTitle("Personal Finance Tracker");
        primaryStage.setScene(new Scene(root, 900, 400));
        primaryStage.show();
    }

    // Prompt 4: Utility for alerts
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 