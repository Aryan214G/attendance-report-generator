package com.attendance.ui;

import com.attendance.service.AttendanceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;

//import static com.attendance.main.App.service;

public class ExcelLoaderController {
    private final AttendanceService service = new AttendanceService();

    MainController mainController = new MainController();

    @FXML
    private Rectangle dropRectangle;
    @FXML
    private Label fileNameLabel;

    @FXML
    private void handleUploadFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Excel File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName());
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            service.loadExcelFile(selectedFile.getAbsolutePath());
        } else {
            fileNameLabel.setText("No file selected");
        }
    }

    @FXML
    public void dragDropZone() {
        // When a file is dragged over the rectangle
        dropRectangle.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                File file = event.getDragboard().getFiles().get(0);
                if (file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls")) {
                    event.acceptTransferModes(TransferMode.COPY);
                    // Remove previous success style and add drag-over if not already present
                    dropRectangle.getStyleClass().remove("drop-success");
                    if (!dropRectangle.getStyleClass().contains("drag-over")) {
                        dropRectangle.getStyleClass().add("drag-over");
                    }
                }
            }
            event.consume();
        });

        // When a file is dropped onto the rectangle
        dropRectangle.setOnDragDropped(event -> {
            var db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                File file = db.getFiles().get(0);
                if (file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls")) {
                    fileNameLabel.setText(file.getName());
                    System.out.println("Dropped file: " + file.getAbsolutePath());
                    service.loadExcelFile(file.getAbsolutePath());
                    success = true;

                    // Update visual feedback
                    dropRectangle.getStyleClass().remove("drag-over");
                    if (!dropRectangle.getStyleClass().contains("drop-success")) {
                        dropRectangle.getStyleClass().add("drop-success");
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // Optional: reset drag-over style if drag exits the rectangle
        dropRectangle.setOnDragExited(event -> {
            dropRectangle.getStyleClass().remove("drag-over");
            event.consume();
        });
    }

    @FXML
    private void switchToHomeScene(ActionEvent event) {
        try {
            mainController.switchToHomeScene(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToFormScene(ActionEvent event) {
        try {
            mainController.switchToFormScene(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
