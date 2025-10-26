package com.attendance.util;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PasswordPrompt {

    private static final String CONFIG_PATH = "src/main/resources/config.properties";
    private static String AUTH_PASSWORD = "admin123"; // default fallback password

    // Static block to load password from config file
    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            Properties props = new Properties();
            props.load(fis);

            String filePassword = props.getProperty("auth.password");
            if (filePassword != null && !filePassword.isBlank()) {
                AUTH_PASSWORD = filePassword.trim();
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load config.properties. Using default password.");
        }
    }

    public static boolean show() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Authorization Required");
        dialog.setHeaderText("Enter password to continue");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Password:"), 0, 0);
        grid.add(passwordField, 1, 0);
        grid.add(errorLabel, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String enteredPassword = passwordField.getText();

            if (!enteredPassword.equals(AUTH_PASSWORD)) {
                errorLabel.setText("Incorrect password. Try again.");
                errorLabel.setVisible(true);
                event.consume(); // prevents dialog from closing
            }
        });

        passwordField.requestFocus();
        dialog.showAndWait();

        return passwordField.getText().equals(AUTH_PASSWORD);
    }
}
