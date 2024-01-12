package soft2412.a2.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import soft2412.a2.App;
import soft2412.a2.database.UserManager;
import soft2412.a2.model.User;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.sql.SQLException;

public class SecondRegisterController {
    @FXML
    public Button registerButton;
    @FXML
    public Button loginButton;
    @FXML
    private Label registerTitle;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private Label messageLabel;
    private App app;

    private String username;
    private String password;
    private String keyID;
    private boolean isAdmin;

    // Rejex pattern for email validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    // Rejex pattern for name
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z-' ]+$");
    //Rejex pattern for phone number
    private static final Pattern AUSTRALIAN_PHONE_PATTERN = Pattern.compile("^(\\(0[0-9]\\) [0-9]{4} [0-9]{4}|04[0-9]{2} [0-9]{3} [0-9]{3}|04[0-9]{8})$");

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
        }
    }
    //email validation
    public boolean isValidEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
    //name validation
    public boolean isValidName(String name) {
        Matcher matcher = NAME_PATTERN.matcher(name);
        return matcher.matches();
    }
    //phone number validation
    public boolean isValidAustralianPhoneNumber(String phoneNumber) {
        Matcher matcher = AUSTRALIAN_PHONE_PATTERN.matcher(phoneNumber);
        return matcher.matches();
    }

    @FXML
    private void handleRegistrationSecondPage(ActionEvent event) throws SQLException {
        this.app = App.getApplicationInstance();

        // TODO: Input validation
        String email = emailField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phoneNumber = phoneNumberField.getText();

        if (email.equals("") || firstName.equals("") || lastName.equals("") || phoneNumber.equals("")) {
            try {
                messageLabel.setText("Please try again");
                messageLabel.getStyleClass().add("error-message");
            } catch (NumberFormatException e) {
                messageLabel.setText("Invalid input");
                messageLabel.getStyleClass().add("error-message");
            }
        } else if(!isValidEmail(email)){
            messageLabel.setText("Invalid email format.");
            messageLabel.getStyleClass().add("error-message");
        } else if(!isValidName(firstName) || !isValidName(lastName)){
            messageLabel.setText("First name or last name contains invalid characters.");
            messageLabel.getStyleClass().add("error-message");
        } else if(!isValidAustralianPhoneNumber(phoneNumber)){
            messageLabel.setText("Invalid Australian phone number format.");
            messageLabel.getStyleClass().add("error-message");
        }
        else {
            UserManager userManager = new UserManager(app.getConn());
            User currentUser = userManager.createNewUser(username, email, phoneNumber, keyID, firstName, lastName, password, "Standard");
            if (!isAdmin) {
                app.login(currentUser);
            } else {
                app.loadAdminDashboard();
            }
        }
    }

    public void initializeData(boolean isAdmin, String username, String password, String keyID) {
        this.isAdmin = isAdmin;
        this.username = username;
        this.password = password;
        this.keyID = keyID;
    }

    @FXML
    private void returnToLogin(ActionEvent event) {
        this.app = App.getApplicationInstance();
        app.loadLoginPage();
    }
}
