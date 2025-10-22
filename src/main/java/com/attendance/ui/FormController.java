package com.attendance.ui;

import com.attendance.service.AttendanceService;
import com.attendance.ui.AppContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class FormController {

    @FXML
    private TextField monthField, yearField, workingDaysField, workingHoursField;

    private final AttendanceService service = AppContext.getAttendanceService();

    @FXML
    private void onNext(ActionEvent event) throws IOException {
        // Read user input
        int month = Integer.parseInt(monthField.getText());
        int year = Integer.parseInt(yearField.getText());
        int workingDays = Integer.parseInt(workingDaysField.getText());
        double workingHours = Double.parseDouble(workingHoursField.getText());

        // Save in AppContext
        AppContext.setMonth(month);
        AppContext.setYear(year);
        AppContext.setWorkingDays(workingDays);
        AppContext.setWorkingHoursPerDay(workingHours);

        // Load Excel and generate report
        service.loadExcelFile(AppContext.getSelectedExcelFile().getAbsolutePath());
        service.setWorkingDays(workingDays);
        service.setWorkingHours(workingHours);
        service.generateReport();

        AppContext.setReportData(service.getLastGeneratedReport());
    }

    private void loadScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root);
        String css = this.getClass().getResource("/com/attendance/ui/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void switchToReportScene(ActionEvent event) throws IOException {
        loadScene(event, "/com/attendance/ui/ReportView.fxml");
    }
    public void switchToExcelLoaderSceneFromFormScene(ActionEvent event) throws IOException {
        loadScene(event, "/com/attendance/ui/ExcelLoaderView.fxml");
    }
}
