package com.attendance.ui;

import com.attendance.model.ReportRow;
import com.attendance.reader.AttendanceReader;
import com.attendance.report.ReportExporter;
import com.attendance.report.ReportGenerator;
import com.attendance.service.AttendanceService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainController {

    private AttendanceService service = new AttendanceService();
    private ReportGenerator generator = new ReportGenerator();
    private ReportExporter exporter = new ReportExporter();

    private List<ReportRow> lastReport;

    @FXML
    public void onLoadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            service.loadExcelFile(file.getAbsolutePath());
            showAlert("File Loaded", "Attendance file loaded successfully!");
        }
    }

    @FXML
    public void onGenerateReport() {
//        lastReport = generator.generateReport(service.getAttendanceRecords());
        showAlert("Report Generated", "Report generated successfully!");
    }

    @FXML
    public void onViewReport() {
        if (lastReport == null) {
            showAlert("Error", "No report generated yet!");
            return;
        }
        // TODO: open a new window to display the table of report data
        showAlert("View Report", "Feature coming soon!");
    }

    @FXML
    public void onAddHours() {
        // TODO: open dialog to select employee and add hours
        showAlert("Add Hours", "Feature coming soon!");
    }

    @FXML
    public void onSaveAsCsv() {
        if (lastReport == null) {
            showAlert("Error", "No report to save!");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                exporter.saveReportAsCsv(lastReport, file);
                showAlert("Saved", "CSV report saved!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to save CSV!");
            }
        }
    }

    @FXML
    public void onSaveAsPdf() {
        if (lastReport == null) {
            showAlert("Error", "No report to save!");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = chooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                exporter.saveReportAsPDF(lastReport, file, 10, 2025);
                showAlert("Saved", "PDF report saved!");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to save PDF!");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
