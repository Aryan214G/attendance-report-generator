package com.attendance.util;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class PasswordPrompt {

    private static final String CONFIG_FILE =
            System.getProperty("user.home") + File.separator + ".attendanceAppConfig.properties";
    private static String AUTH_PASSWORD_HASH;
    private static boolean firstRun = false;

    static {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);

        if (!configFile.exists()) {
            // First run: create config file with default password
            firstRun = true;
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                props.setProperty("auth.passwordHash", hashPassword("admin123"));
                props.store(fos, "Attendance app configuration");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
            AUTH_PASSWORD_HASH = props.getProperty("auth.passwordHash");
        } catch (IOException e) {
            e.printStackTrace();
            AUTH_PASSWORD_HASH = "";
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
            if (!hashPassword(enteredPassword).equals(AUTH_PASSWORD_HASH)) {
                errorLabel.setText("Incorrect password. Try again.");
                errorLabel.setVisible(true);
                event.consume();
            } else {
                dialog.setResult(null);
            }
        });

        passwordField.requestFocus();
        dialog.showAndWait();

        return hashPassword(passwordField.getText()).equals(AUTH_PASSWORD_HASH);
    }

    public static void changePassword() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter new password");

        ButtonType okButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Old password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int row = 0;
        if (!firstRun) {
            grid.add(new Label("Old Password:"), 0, row);
            grid.add(oldPasswordField, 1, row++);
        }

        grid.add(new Label("New Password:"), 0, row);
        grid.add(newPasswordField, 1, row++);
        grid.add(new Label("Confirm Password:"), 0, row);
        grid.add(confirmPasswordField, 1, row++);
        grid.add(errorLabel, 1, row);

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);

        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String oldPass = oldPasswordField.getText();
            String newPass = newPasswordField.getText();
            String confirmPass = confirmPasswordField.getText();

            if (!firstRun && !hashPassword(oldPass).equals(AUTH_PASSWORD_HASH)) {
                errorLabel.setText("Old password incorrect!");
                errorLabel.setVisible(true);
                event.consume();
            } else if (!newPass.equals(confirmPass)) {
                errorLabel.setText("Passwords do not match!");
                errorLabel.setVisible(true);
                event.consume();
            } else if (newPass.isBlank()) {
                errorLabel.setText("Password cannot be empty!");
                errorLabel.setVisible(true);
                event.consume();
            } else {
                try {
                    updatePasswordInConfig(newPass);
                    AUTH_PASSWORD_HASH = hashPassword(newPass);
                    firstRun = false; // no longer first run
                } catch (IOException e) {
                    errorLabel.setText("Failed to update password!");
                    errorLabel.setVisible(true);
                    event.consume();
                }
            }
        });

        newPasswordField.requestFocus();
        dialog.showAndWait();
    }

    private static void updatePasswordInConfig(String newPassword) throws IOException {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
        }
        props.setProperty("auth.passwordHash", hashPassword(newPassword));
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            props.store(fos, "Updated password hash");
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
