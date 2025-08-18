package com.attendance.reader;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.File;
import java.util.Map;

public class AttendanceApp extends Application {
    private AttendanceService attendanceService;
    private TextArea reportArea;
    private Map<String, EmployeeStats> reportData;
    
    @Override
    public void start(Stage primaryStage) {
        attendanceService = new AttendanceService();
        
        // Create main layout with title
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        // Title
        Label titleLabel = new Label("Attendance Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        // File Section
        TitledPane fileSection = createFileSection();
        
        // Configuration Section
        TitledPane configSection = createConfigSection();
        
        // Add Hours Section
        TitledPane addHoursSection = createAddHoursSection();
        
        // Report Section
        TitledPane reportSection = createReportSection();
        
        mainLayout.getChildren().addAll(
            titleLabel,
            fileSection,
            configSection,
            addHoursSection,
            reportSection
        );
        
        Scene scene = new Scene(mainLayout, 800, 900);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        primaryStage.setTitle("Attendance Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private TitledPane createFileSection() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        TextField filePathField = new TextField();
        filePathField.setEditable(false);
        filePathField.setPrefWidth(400);
        
        Button loadFileBtn = new Button("Browse Excel File");
        loadFileBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        HBox fileBox = new HBox(10);
        fileBox.setAlignment(Pos.CENTER_LEFT);
        fileBox.getChildren().addAll(filePathField, loadFileBtn);
        
        content.getChildren().add(fileBox);
        
        loadFileBtn.setOnAction(e -> handleFileLoad(filePathField));
        
        TitledPane section = new TitledPane("File Selection", content);
        section.setExpanded(true);
        return section;
    }
    
    private TitledPane createConfigSection() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        
        TextField hoursPerDayField = new TextField();
        TextField workingDaysField = new TextField();
        
        grid.add(new Label("Hours per Day:"), 0, 0);
        grid.add(hoursPerDayField, 1, 0);
        grid.add(new Label("Working Days:"), 0, 1);
        grid.add(workingDaysField, 1, 1);
        
        TitledPane section = new TitledPane("Configuration", grid);
        section.setExpanded(true);
        return section;
    }
    
    private TitledPane createAddHoursSection() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField employeeNameField = new TextField();
        TextField hoursToAddField = new TextField();
        
        grid.add(new Label("Employee Name:"), 0, 0);
        grid.add(employeeNameField, 1, 0);
        grid.add(new Label("Hours to Add:"), 0, 1);
        grid.add(hoursToAddField, 1, 1);
        
        Button addHoursBtn = new Button("Add Hours");
        addHoursBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        
        content.getChildren().addAll(grid, addHoursBtn);
        
        addHoursBtn.setOnAction(e -> handleAddHours(employeeNameField, hoursToAddField));
        
        TitledPane section = new TitledPane("Add Hours", content);
        section.setExpanded(true);
        return section;
    }
    
    private TitledPane createReportSection() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefRowCount(10);
        reportArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12px;");
        
        HBox buttonBox = new HBox(10);
        Button generateReportBtn = new Button("Generate Report");
        Button saveReportBtn = new Button("Export to CSV");
        generateReportBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        saveReportBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        buttonBox.getChildren().addAll(generateReportBtn, saveReportBtn);
        content.getChildren().addAll(buttonBox, reportArea);
        
        generateReportBtn.setOnAction(e -> handleGenerateReport());
        saveReportBtn.setOnAction(e -> handleSaveReport());
        
        TitledPane section = new TitledPane("Report", content);
        section.setExpanded(true);
        return section;
    }
    
    private void handleFileLoad(TextField filePathField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                attendanceService.loadExcelFile(file.getAbsolutePath());
                filePathField.setText(file.getAbsolutePath());
            } catch (Exception ex) {
                showError("Error", "Failed to load file: " + ex.getMessage());
            }
        }
    }
    
    private void handleAddHours(TextField nameField, TextField hoursField) {
        try {
            String name = nameField.getText();
            double hours = Double.parseDouble(hoursField.getText());
            // TODO: Implement add hours logic
            nameField.clear();
            hoursField.clear();
        } catch (Exception ex) {
            showError("Error", "Failed to add hours: " + ex.getMessage());
        }
    }
    
    private void handleGenerateReport() {
        // TODO: Implement report generation
        reportArea.setText("Report generation not implemented yet");
    }
    
    private void handleSaveReport() {
        if (reportData == null) {
            showError("Error", "No report data available");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                CSVStorage.save(reportData, file.getAbsolutePath());
            } catch (Exception ex) {
                showError("Error", "Failed to save report: " + ex.getMessage());
            }
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}