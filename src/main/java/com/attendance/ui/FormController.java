package com.attendance.ui;

import com.attendance.model.ReportRow;
import com.attendance.service.AttendanceService;
import com.attendance.ui.AppContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FormController implements Initializable {

    @FXML
    private TextField monthField, yearField, workingDaysField, workingHoursField;

    @FXML
    private ChoiceBox<String> monthChoiceBox;

    @FXML
    private Label defaultHoursLabel;

    private final AttendanceService service = AppContext.getAttendanceService();

    @FXML
    private void onNext(ActionEvent event) throws IOException {
        // Read user input
        String month = monthChoiceBox.getValue();
        int year = Integer.parseInt(yearField.getText());
        int workingDays = Integer.parseInt(workingDaysField.getText());
        double workingHours = 8; //default
        if (!workingHoursField.getText().isEmpty())
        {
            workingHours = Double.parseDouble(workingHoursField.getText());
        }

        // Save in AppContext
        AppContext.setMonth(month);
        AppContext.setYear(year);
        AppContext.setWorkingDays(workingDays);
        AppContext.setWorkingHoursPerDay(workingHours);
//        System.out.println("working hours: "+workingHours);
        // Load Excel and generate report
        service.loadExcelFile(AppContext.getSelectedExcelFiles().getAbsolutePath());
        service.setWorkingDays(workingDays);
        service.setWorkingHours(workingHours);

//        service.generateReport();

        List<ReportRow> report = service.generateReport();
        AppContext.setReportData(report);
        System.out.println("Report rows: " + report.size());
        for (ReportRow row : report) {
            System.out.println(row.getEmployeeName());
        }
        // Switch to Report Scene
        switchToReportScene(event);
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        monthChoiceBox.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );
        workingHoursField.setPromptText("Default: 8 hours");

        boolean debug = java.lang.management.ManagementFactory.getRuntimeMXBean()
                .getInputArguments().toString().contains("jdwp");

        if (debug) {
            javafx.application.Platform.runLater(() -> {
                // Auto-fill the form in DEBUG mode
                monthChoiceBox.setValue("December");
                yearField.setText("2025");
                workingDaysField.setText("25");
                workingHoursField.setText("8");

                // OPTIONAL:
                // Automatically press "Next" after filling in values
                // onNext(null);  // uncomment if you want auto-skip
            });
        }
    }
}
