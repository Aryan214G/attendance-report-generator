package com.attendance.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import javax.swing.*;
import java.io.File;

public class ExcelLoaderController {


    MainController mainController = new MainController();

    @FXML
    private Rectangle dropRectangle;
    @FXML
    private Label fileNameLabel;

    @FXML
    private void handleUploadFile(ActionEvent event)
    {
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
            // TODO: Process file (load Excel, display, etc.)
        } else {
            fileNameLabel.setText("No file selected");
        }
    }

    @FXML
    public void dragDropZone() {
        // When a file is dragged over the rectangle
        dropRectangle.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                // Only accept Excel files
                File file = event.getDragboard().getFiles().get(0);
                if (file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls")) {
                    event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
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
                // Only process Excel files
                if (file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls")) {
                    fileNameLabel.setText(file.getName());
                    System.out.println("Dropped file: " + file.getAbsolutePath());
                    // TODO: Add code to process the Excel file
                    success = true;
                }
            }
            event.setDropCompleted(success);
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
