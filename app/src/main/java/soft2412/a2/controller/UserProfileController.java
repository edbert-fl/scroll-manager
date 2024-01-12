package soft2412.a2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import soft2412.a2.App;
import soft2412.a2.ExcludeFromJacocoGeneratedReport;
import soft2412.a2.database.UserManager;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.model.Scroll;
import soft2412.a2.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class UserProfileController extends PageController {
    public Button userProfile;
    public Button adminDashboardButton;
    public TableView<Scroll> tableView;
    public TableColumn<Scroll, LocalDateTime> dateColumn;
    public TableColumn<Scroll, String> fileNameColumn;
    public TableColumn<Scroll, String> uploaderColumn;
    public TableColumn<Scroll, String> downloadsColumn;
    public TableColumn<Scroll, Void> downloadButtonColumn;
    public TableColumn<Scroll, Void> editButtonColumn;
    public TableColumn<Scroll, String> scrollNameColumn;
    public TableColumn<Scroll, Void> deleteButtonColumn;
    public Label keyIDLabel;
    public Label firstNameLabel;
    public Label lastNameLabel;
    public Label emailLabel;
    public Label phoneNumberLabel;
    public Label accountTypeLabel;
    public Label messageLabel;
    private App app;
    private User user;
    private User adminAccount;

    public void initialize() {
        if (user != null) {
            keyIDLabel.setText(user.getKeyID());
            firstNameLabel.setText(user.getFirstName());
            lastNameLabel.setText(user.getLastName());
            emailLabel.setText(user.getEmail());
            phoneNumberLabel.setText(user.getPhoneNumber());
            accountTypeLabel.setText(user.getAccountType());


            App app = App.getApplicationInstance();

            if (user.getAccountType().equals("Admin")) {
                userProfile.setText(user.getUsername());
                adminDashboardButton.setVisible(true);
            } else if((adminAccount != null) && (adminAccount.getAccountType().equals("Admin"))) {
                userProfile.setText(adminAccount.getUsername());
                adminDashboardButton.setVisible(true);
            } else {
                userProfile.setText(user.getUsername());
            }

            UserManager userManager = new UserManager(app.getConn());
            ScrollsManager scrollsManager = new ScrollsManager(app.getConn());

            // Initialize columns and set cell value factories
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("uploadDate"));
            scrollNameColumn.setCellValueFactory(new PropertyValueFactory<>("scrollName"));
            fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
            uploaderColumn.setCellValueFactory(new PropertyValueFactory<>("uploaderName"));
            downloadsColumn.setCellValueFactory(new PropertyValueFactory<>("downloads"));
            downloadButtonColumn.setCellFactory(param -> new TableCell<Scroll, Void>() {
                private final Button downloadButton = new Button("Download");

                {
                    downloadButton.setOnAction(event -> {
                        int scrollID = getTableView().getItems().get(getIndex()).getScrollID();
                        downloadScroll(scrollID); // Implement this method
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(downloadButton);
                    }
                }
            });
            editButtonColumn.setCellFactory(param -> new TableCell<Scroll, Void>() {
                private final Button editButton = new Button("Edit");

                {
                    editButton.setOnAction(event -> {
                        Scroll scroll = getTableView().getItems().get(getIndex());
                        loadEditScrollPage(scroll);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(editButton);
                    }
                }
            });
            deleteButtonColumn.setCellFactory(param -> new TableCell<Scroll, Void>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.setOnAction(event -> {
                        Scroll scroll = getTableView().getItems().get(getIndex());
                        try {
                            scrollsManager.deleteScroll(scroll, app.getActiveUser());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        initialize();
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            });

            // Load data into the TableView
            tableView.setItems(getMyScrollData());
        }
    }

    private ObservableList<Scroll> getMyScrollData() {
        app = App.getApplicationInstance();
        Connection conn = app.getConn();
        ScrollsManager scrollsManager = new ScrollsManager(conn);
        ObservableList<Scroll> observableList = null;
        try {
            observableList = FXCollections.observableArrayList(scrollsManager.getScrollsForUser(user));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return observableList;
    }

    public void editKeyID(ActionEvent actionEvent) {
        keyIDLabel.setText(app.showEditPopup("Key ID", user));
    }

    public void editFirstName(ActionEvent actionEvent) {
        firstNameLabel.setText(app.showEditPopup("First Name", user));
    }

    public void editLastName(ActionEvent actionEvent) {
        lastNameLabel.setText(app.showEditPopup("Last Name", user));
    }

    public void editEmail(ActionEvent actionEvent) {
        emailLabel.setText(app.showEditPopup("Email", user));
    }

    public void editPhoneNumber(ActionEvent actionEvent) {
        phoneNumberLabel.setText(app.showEditPopup("Phone Number", user));
    }

    public void editPassword(ActionEvent actionEvent) {
        app.showEditPasswordPopup(user);
    }

    public void saveChanges(ActionEvent actionEvent) {
        String newKeyID = keyIDLabel.getText();
        String newFirstName = firstNameLabel.getText();
        String newLastName = lastNameLabel.getText();
        String newEmail = emailLabel.getText();
        String newPhoneNumber = phoneNumberLabel.getText();

        if (newKeyID.equals("") || newFirstName.equals("") || newLastName.equals("") || newEmail.equals("") || newPhoneNumber.equals("") ||
            newKeyID == null    || newFirstName == null    || newLastName == null    || newEmail == null    || newPhoneNumber == null) {
            messageLabel.setText("Please make sure all fields are not empty!");
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        UserManager userManager = new UserManager(app.getConn());
        try {
            userManager.updateUser(user, newKeyID, newFirstName, newLastName, newEmail, newPhoneNumber);
        } catch (SQLException e) {
            messageLabel.setText("Error keyID already in use!");
            messageLabel.getStyleClass().add("error-message");
        }

        messageLabel.setText("Success!");
        messageLabel.getStyleClass().remove("error-message");
        User newActiveUser = null;
        try {
            newActiveUser = userManager.findUserByID(newKeyID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        app.loadUserProfile(app.getActiveUser(), newActiveUser);
        initialize();
    }

    public void cancelChanges(ActionEvent actionEvent) {
        initialize();
    }

    public void initializeData(User adminAccount, User user) {
        this.adminAccount = adminAccount;
        this.user = user;
    }
}
