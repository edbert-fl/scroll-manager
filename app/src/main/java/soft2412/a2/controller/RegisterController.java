
package soft2412.a2.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import soft2412.a2.App;
import soft2412.a2.database.UserManager;

import java.sql.SQLException;

public class RegisterController {
    @FXML
    public Button loginButton;
    @FXML
    public Button continueRegistrationButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField keyIDField;
    @FXML
    private Label messageLabel;
    @FXML
    private Label registerTitle;

    private App app;
    boolean isAdmin;

    public void initialize() {
        if (this.isAdmin) {
            registerTitle.setText("Create User");
            loginButton.setText("Back to Admin Dashboard");
            loginButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    app = App.getApplicationInstance();
                    app.loadAdminDashboard();
                }});
            GridPane.setColumnSpan(continueRegistrationButton, 2);
            continueRegistrationButton.getStyleClass().remove("short-login-button");
            continueRegistrationButton.getStyleClass().add("login-button");
        }
    }

    @FXML
    private void handleRegistrationFirstPage(ActionEvent event) throws SQLException {
        this.app = App.getApplicationInstance();

        UserManager userManager = new UserManager(app.getConn());

        String currentUsername = usernameField.getText();
        String currentPassword = passwordField.getText();
        String currentKeyID = keyIDField.getText();

        String confirmPassword = confirmPasswordField.getText();

        //Some Error checking to make sure that the user inputs are correct.
        if (currentUsername.equals("") || currentPassword.length() < 8 || currentKeyID.equals("")) {
            messageLabel.setText("Please try again");
            messageLabel.getStyleClass().add("error-message");
        } else if (userManager.usernameExists(currentUsername)) {
            messageLabel.setText("Username already exists!");
            messageLabel.getStyleClass().add("error-message");
        } else if (userManager.keyIDExists(currentKeyID)) {
            messageLabel.setText("Key ID already exists!");
            messageLabel.getStyleClass().add("error-message");
        } else {
            if (currentPassword.equals(confirmPassword)) {
                messageLabel.setText("Continuing registration...");
                messageLabel.getStyleClass().remove("error-message");

                app.loadSecondRegisterPage(isAdmin, currentUsername, currentPassword, currentKeyID);
            } else {
                messageLabel.setText("Passwords do not match. Please try again.");
                messageLabel.getStyleClass().add("error-message");
            }
        }
    }

    @FXML
    private void returnToLogin(ActionEvent event) {
        app = App.getApplicationInstance();
        app.loadLoginPage();
    }

    public void initializeData(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
