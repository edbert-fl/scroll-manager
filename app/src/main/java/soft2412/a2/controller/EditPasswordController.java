package soft2412.a2.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import soft2412.a2.App;
import soft2412.a2.database.UserManager;
import soft2412.a2.model.User;
import soft2412.a2.utility.PopupWindowController;

import java.sql.SQLException;

public class EditPasswordController {
    public Label messageLabel;
    public TextField textField;
    public String result;
    public User user;
    public PasswordField passwordField;
    public PasswordField passwordConfirmField;
    private Stage stage;

    public void initializeData(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public String getResult() {
        return result;
    }

    public void handleSave(ActionEvent actionEvent) {
        String password = passwordField.getText();
        String passwordConfirm = passwordConfirmField.getText();
        if (!password.equals(passwordConfirm)) {
            messageLabel.setText("Passwords don't match!");
            messageLabel.getStyleClass().add("error-message");
        } else if (password.length() < 8) {
            messageLabel.setText("Passwords must be at least 8 characters");
            messageLabel.getStyleClass().add("error-message");
        } else {
            App app = App.getApplicationInstance();
            UserManager userManager = new UserManager(app.getConn());
            try {
                userManager.updatePassword(user, password);
                PopupWindowController popupWindowController = new PopupWindowController("Successfully changed password!", 300, 100);
                popupWindowController.showAndWait();
                stage.close();
            } catch (SQLException e) {
                PopupWindowController popupWindowController = new PopupWindowController("Error could not update password! Try again.", 300, 100);
                popupWindowController.showAndWait();
                stage.close();
            }
        }
    }

    public void initialize() {}
}
