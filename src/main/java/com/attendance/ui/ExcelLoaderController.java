package com.attendance.ui;

import com.attendance.service.AttendanceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelLoaderController {

    private final AttendanceService service = AppContext.getAttendanceService();
    private final SceneSwitcherController sceneSwitcherController = new SceneSwitcherController();

    @FXML
    private Rectangle dropRectangle;

    @FXML
    private ListView<String> filesListView;

    // ----------------------------------
    // Initialization (debug support)
    // ----------------------------------
    @FXML
    public void initialize() {
        boolean debug = java.lang.management.ManagementFactory
                .getRuntimeMXBean()
                .getInputArguments()
                .toString()
                .contains("jdwp");

        if (debug) {
            List<File> testFiles = List.of(
                    new File("E:/projects/attendance project files/night shift/dec-2025-pwt.xlsx"),
                    new File("E:/projects/attendance project files/night shift/dec-ccd.xlsx")
            );

            AppContext.setSelectedExcelFiles(testFiles);
            filesListView.getItems().setAll(
                    testFiles.stream().map(File::getName).toList()
            );

            dropRectangle.getStyleClass().add("drop-success");
        }
    }

    // ----------------------------------
    // File chooser (multiple selection)
    // ----------------------------------
    @FXML
    private void handleUploadFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Excel Files");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);


        List<File> excelFiles = filterExcelFiles(selectedFiles);


        filesListView.getItems().setAll(
                excelFiles.stream().map(File::getName).toList()
        );

        excelFiles.forEach(file ->
                System.out.println("Selected file: " + file.getAbsolutePath())
        );

        AppContext.setSelectedExcelFiles(excelFiles);
        dropRectangle.getStyleClass().add("drop-success");
    }

    // ----------------------------------
    // Drag & Drop zone
    // ----------------------------------
    @FXML
    public void dragDropZone() {

        // Drag over
        dropRectangle.setOnDragOver(event -> {
            List<File> excelFiles = extractExcelFiles(event.getDragboard());

            if (!excelFiles.isEmpty()) {
                event.acceptTransferModes(TransferMode.COPY);
                dropRectangle.getStyleClass().remove("drop-success");
                if (!dropRectangle.getStyleClass().contains("drag-over")) {
                    dropRectangle.getStyleClass().add("drag-over");
                }
            }
            event.consume();
        });

        // Drop
        dropRectangle.setOnDragDropped(event -> {
            List<File> excelFiles = extractExcelFiles(event.getDragboard());
            boolean success = false;

            if (!excelFiles.isEmpty()) {
                filesListView.getItems().setAll(
                        excelFiles.stream().map(File::getName).toList()
                );

                excelFiles.forEach(file ->
                        System.out.println("Dropped file: " + file.getAbsolutePath())
                );

                AppContext.setSelectedExcelFiles(excelFiles);
                success = true;

                dropRectangle.getStyleClass().remove("drag-over");
                if (!dropRectangle.getStyleClass().contains("drop-success")) {
                    dropRectangle.getStyleClass().add("drop-success");
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });

        // Drag exit
        dropRectangle.setOnDragExited(event -> {
            dropRectangle.getStyleClass().remove("drag-over");
            event.consume();
        });
    }

    // ----------------------------------
    // Helpers
    // ----------------------------------
    private List<File> extractExcelFiles(Dragboard dragboard) {
        if (!dragboard.hasFiles()) {
            return List.of();
        }
        return filterExcelFiles(dragboard.getFiles());
    }

    private List<File> filterExcelFiles(List<File> files) {
        return files.stream()
                .filter(file -> file.getName().toLowerCase().endsWith(".xlsx"))
                .toList();
    }

    // ----------------------------------
    // Scene navigation
    // ----------------------------------
    private void loadScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root);
        String css = getClass().getResource("/com/attendance/ui/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToHomeScene(ActionEvent event) throws IOException {
        loadScene(event, "/com/attendance/ui/MainView.fxml");
    }

    @FXML
    public void switchToFormScene(ActionEvent event) throws IOException {
        loadScene(event, "/com/attendance/ui/FormView.fxml");
    }
}
