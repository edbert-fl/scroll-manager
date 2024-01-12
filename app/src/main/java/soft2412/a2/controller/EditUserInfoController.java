package soft2412.a2.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import soft2412.a2.model.User;

public class EditUserInfoController {
    public Label messageLabel;
    public TextField textField;
    public String attribute;
    public String result;
    public User user;
    private Stage stage;

    public void initialize() {
        if (attribute != null && user != null) {
            messageLabel.setText(this.attribute);
            switch (this.attribute) {
                case "First Name" -> textField.setText(user.getFirstName());
                case "Last Name" -> textField.setText(user.getLastName());
                case "Email" -> textField.setText(user.getEmail());
                case "Phone Number" -> textField.setText(user.getPhoneNumber());
                case "Key ID" -> textField.setText(user.getKeyID());
            }
        }
    }

    public void initializeData(Stage stage, String attribute, User user) {
        this.stage = stage;
        this.attribute = attribute;
        this.user = user;
    }

    public String getResult() {
        return result;
    }

    public void handleSave(ActionEvent actionEvent) {
        this.result = textField.getText();
        stage.close();
    }
}
