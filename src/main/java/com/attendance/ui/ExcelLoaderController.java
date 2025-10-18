package com.attendance.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import javax.swing.*;
import java.io.File;

public class ExcelLoaderController {


    MainController mainController = new MainController();
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
