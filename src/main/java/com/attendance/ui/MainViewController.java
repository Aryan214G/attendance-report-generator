package com.attendance.ui;

import com.attendance.ui.AppContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {

    @FXML
    private MenuBar menuBar;
    private Stage stage;
    private Scene scene;

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
}

