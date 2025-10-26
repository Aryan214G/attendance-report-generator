package com.attendance.ui;

import com.attendance.ui.AppContext;
import com.attendance.util.PasswordPrompt;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainViewController {

    private static final long MAX_RECENT_REPORTS = 8;
    @FXML
    private MenuBar menuBar;
    private Stage stage;
    private Scene scene;
    @FXML
    private GridPane reportGrid;
    @FXML
    private ScrollPane reportScrollPane;


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
        loadRecentReports();
        reportsDir = AppContext.getAttendanceService().getRootDirectory();

        hideReportList();
        updateReportList("");

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue.isEmpty())
            {
                hideReportList();
            }
            else{
                showReportList();
                updateReportList(newValue);

            }
        });

        //for opening selected report
        reportListView.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                String selectedReport = reportListView.getSelectionModel().getSelectedItem();
                if (selectedReport != null) {
                    try {
                        openReport(selectedReport);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        javafx.application.Platform.runLater(() -> {
            searchField.setOnKeyPressed(event -> {
                ;
                switch (event.getCode()) {
                    case ESCAPE -> hideReportList();
                }
            });
            searchField.getScene().addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                Node clickedNode = event.getPickResult().getIntersectedNode();
                if (clickedNode != null && clickedNode != searchField && !isDescendant(reportListView, clickedNode)) {
                    hideReportList();
                }
            });
        });
        
        loadRecentReports();
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

    private void openReport(String selectedReport) throws IOException {
        String filePath;
        if(selectedReport.endsWith(".csv"))
        {
            filePath = reportsDir + File.separator + "CSVReports"
                    + File.separator + selectedReport;
        }
        else
        {
            filePath = reportsDir + File.separator + "PDFReports"
                    + File.separator + selectedReport;
        }

        File file = new File(filePath);
        if(file.exists())
        {
            Desktop.getDesktop().open(file);
        }
    }

    private void loadRecentReports() {
        File csvFolder = new File(reportsDir + "/CSVReports");
        File pdfFolder = new File(reportsDir + "/PDFReports");

        List<File> recentReports = new ArrayList<>();

        if (csvFolder.exists()) {
            File[] csvFiles = csvFolder.listFiles();
            if (csvFiles != null) recentReports.addAll(Arrays.asList(csvFiles));
        }
        if (pdfFolder.exists()) {
            File[] pdfFiles = pdfFolder.listFiles();
            if (pdfFiles != null) recentReports.addAll(Arrays.asList(pdfFiles));
        }

        // Sort by last modified (newest first)
        recentReports.sort((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

        // Keep only the most recent few
        recentReports = recentReports.stream()
                .limit(MAX_RECENT_REPORTS)
                .toList();

        // Clear any old content
        reportGrid.getChildren().clear();

        int col = 0, row = 0;
        for (File report : recentReports) {
            VBox card = createReportCard(report);
            reportGrid.add(card, col, row);

            col++;
            if (col == 4) { // 4 cards per row
                col = 0;
                row++;
            }
        }
    }


    private VBox createReportCard(File report) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(120, 120);
        box.getStyleClass().add("report-card");

        Label name = new Label(report.getName());
        name.setWrapText(true);
        name.setMaxWidth(100);
        name.setStyle("-fx-font-size: 12px; -fx-text-alignment: center;");

        // Optional icon
        Label icon = new Label(report.getName().endsWith(".pdf") ? "📄" : "📊");
        icon.setStyle("-fx-font-size: 30px;");

        box.getChildren().addAll(icon, name);

        // Click to open
        box.setOnMouseClicked(e -> {
            try {
                Desktop.getDesktop().open(report);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Hover effect
        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: #e6f0ff; -fx-background-radius: 10;"));
        box.setOnMouseExited(e -> box.setStyle("-fx-background-color: transparent;"));

        return box;
    }



    private void showReportList() {
        reportListView.setVisible(true);
        reportListView.setManaged(true);
    }
    private void hideReportList() {
        reportListView.setVisible(false);
        reportListView.setManaged(false);
    }

    private boolean isDescendant(Node parent, Node child) {
        while(child != null)
        {
            if(child == parent) return true;
            child = child.getParent();
        }
        return false;
    }

    @FXML
    private void handleChangePassword() {
        PasswordPrompt.changePassword();
    }

}

