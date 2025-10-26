package com.attendance.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/attendance/ui/MainView.fxml"));
        Scene scene = new Scene(root);
        String css = this.getClass().getResource("/com/attendance/ui/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setTitle("Attendance report generator");

        Text text = new Text();
        text.setText("Generate report");
        text.setX(300);
        text.setY(300);
        text.setStyle("-fx-font-size: 30px;");



        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}