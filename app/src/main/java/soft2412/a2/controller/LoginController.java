package soft2412.a2.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import soft2412.a2.App;
import soft2412.a2.database.UserManager;
import soft2412.a2.model.User;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    private App app;

    private User user;

    private Connection conn;


    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        this.app = App.getApplicationInstance();
        this.conn = App.getApplicationInstance().getConn();

        String username = usernameField.getText();
        String password = passwordField.getText();

        UserManager userManager = new UserManager(conn);
        User user = null;
        try {
            user = userManager.findAccount(username, password);
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (user == null) {
            messageLabel.setText("Login failed. Please try again.");
            messageLabel.getStyleClass().add("error-message");
        } else {
            app.login(user);
        }
    }
    @FXML
    private void handleGoToRegisterAction(ActionEvent event) {
        App app = App.getApplicationInstance();
        app.loadRegisterPage();
    }
    @FXML
    private void handleGoToGuestPage(ActionEvent event){
        this.app = App.getApplicationInstance();
        app.handleGoToGuestPage(event);
    }
}
