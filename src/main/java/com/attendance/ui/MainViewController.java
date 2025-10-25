package com.attendance.ui;

import com.attendance.ui.AppContext;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainViewController {

    @FXML
    private MenuBar menuBar;
    private Stage stage;
    private Scene scene;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> reportListView;

    private String reportsDir;

    @FXML
    public void switchToExcelLoaderScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/attendance/ui/ExcelLoaderView.fxml"));
        stage = (Stage) menuBar.getScene().getWindow();
        scene = new Scene(root);
        String css = this.getClass().getResource("/com/attendance/ui/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void openFolderWithReports(ActionEvent event)
    {
        String userHome = System.getProperty("user.home");
        String documentsDir = userHome + File.separator + "Documents";
        String rootDir = documentsDir + File.separator + "AttendanceReports";
        try{
            File reportsDir = new File((rootDir));
            if(Desktop.isDesktopSupported() && reportsDir.exists()) {
                Desktop.getDesktop().open(reportsDir);
            } else {
                System.out.println("Desktop is not supported or directory does not exist.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        AppContext.getAttendanceService().fileDirectoryHelper();
        reportsDir = AppContext.getAttendanceService().getRootDirectory();

        updateReportList("");

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            updateReportList(newValue);
        });
    }

    private void updateReportList(String filter) {
        File csvFolder = new File(reportsDir + "/CSVReports");
        File pdfFolder = new File(reportsDir + "/PDFReports");

        List<String> allReports = new ArrayList<>();
        if (csvFolder.exists()) {
            allReports.addAll(Arrays.stream(csvFolder.list())
                    .filter(name -> name.toLowerCase().contains(filter))
                    .toList());
        }
        if (pdfFolder.exists()) {
            allReports.addAll(Arrays.stream(pdfFolder.list())
                    .filter(name -> name.contains(filter))
                    .toList());
        }

        reportListView.setItems(FXCollections.observableArrayList(allReports));
    }



}

