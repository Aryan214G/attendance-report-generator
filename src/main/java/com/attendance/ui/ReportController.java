package com.attendance.ui;

import com.attendance.model.ReportRow;
import com.attendance.service.AttendanceService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ReportController {

    private final AttendanceService service = AppContext.getAttendanceService();

    @FXML
    private TableView<ReportRow> reportTable;
    @FXML
    private TableColumn<ReportRow, String> employeeNameCol;
    @FXML
    private TableColumn<ReportRow, Double> hoursWorkedCol;
    @FXML
    private TableColumn<ReportRow, Double> hoursAddedCol;
    @FXML
    private TableColumn<ReportRow, Double> totalHoursCol;
    @FXML
    private TableColumn<ReportRow, Integer> daysWorkedCol;
    @FXML
    private TableColumn<ReportRow, Integer> workingDaysCol;
    @FXML
    private TableColumn<ReportRow, Double> overtimeCol;
    @FXML
    private TableColumn<ReportRow, Integer> singleCheckInsCol;

    private SceneSwitcherController sceneSwitcherController = new SceneSwitcherController();

    @FXML
    public void initialize() {
        // Bind columns to model
        employeeNameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        hoursWorkedCol.setCellValueFactory(new PropertyValueFactory<>("hoursWorked"));
        hoursAddedCol.setCellValueFactory(new PropertyValueFactory<>("hoursAdded"));
        totalHoursCol.setCellValueFactory(new PropertyValueFactory<>("totalHoursWorked"));
        daysWorkedCol.setCellValueFactory(new PropertyValueFactory<>("daysWorked"));
        workingDaysCol.setCellValueFactory(new PropertyValueFactory<>("workingDaysInMonth"));
        overtimeCol.setCellValueFactory(new PropertyValueFactory<>("overtimeHours"));
        singleCheckInsCol.setCellValueFactory(new PropertyValueFactory<>("singleCheckInDays"));

        // Load report from AppContext
        List<ReportRow> reportData = AppContext.getReportData();
        if (reportData != null) {
            reportTable.setItems(FXCollections.observableArrayList(reportData));
        }
    }

    @FXML
    private void handleAddHours(ActionEvent event) {
        ReportRow selected = reportTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select an employee first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Hours");
        dialog.setHeaderText("Add hours to " + selected.getEmployeeName());
        dialog.setContentText("Enter number of hours:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(hoursStr -> {
            try {
                double hours = Double.parseDouble(hoursStr);
                service.addHours(selected.getEmployeeName(), hours);
                reportTable.refresh();
            } catch (NumberFormatException e) {
                showAlert("Invalid number format.");
            }
        });
    }

    @FXML
    private void handleSaveReport(ActionEvent event) {
        List<ReportRow> reportData = AppContext.getReportData();
        if (reportData == null || reportData.isEmpty()) {
            showAlert("No report data to save.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("CSV", "CSV", "PDF");
        dialog.setTitle("Save Report");
        dialog.setHeaderText("Select format:");
        dialog.setContentText("Format:");

        Optional<String> choice = dialog.showAndWait();
        choice.ifPresent(format -> {
            int option = format.equals("CSV") ? 1 : 2;
            try {
                service.saveReport(option, AppContext.getMonth(), AppContext.getYear());
                showAlert("Report saved successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error saving report: " + e.getMessage());
            }
        });
    }

    @FXML
    private void switchToHomeScene(ActionEvent event) {
        try {
            sceneSwitcherController.switchToHomeScene(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
