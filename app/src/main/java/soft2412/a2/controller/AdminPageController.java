package soft2412.a2.controller;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import soft2412.a2.App;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.database.UserManager;
import soft2412.a2.model.Scroll;
import soft2412.a2.model.User;

import java.sql.Connection;
import java.sql.SQLException;

public class AdminPageController extends PageController {
    @FXML
    public Label downloadStatLabel;
    @FXML
    public Label totalDownloadStat;
    @FXML
    public Label totalUserStatLabel;
    @FXML
    public Label totalUserStat;
    @FXML
    public Label uploadStatLabel;
    @FXML
    public Label totalScrollsStat;
    @FXML
    public Button adminDashboardButton;
    @FXML
    public TableView<User> tableView;
    @FXML
    public TableColumn<User, String> keyIDColumn;
    @FXML
    public TableColumn<User, String> usernameColumn;
    @FXML
    public TableColumn<User, String> emailColumn;
    @FXML
    public TableColumn<User, Integer> numberOfScrolls;
    @FXML
    public TableColumn<User, String> firstNameColumn;
    @FXML
    public TableColumn<User, String> lastNameColumn;
    @FXML
    public TableColumn<User, String> accountTypeColumn;
    @FXML
    public TableColumn<User, Void> editUserButtonColumn;
    public TableColumn<User, Void> changeAccountTypeButtonColumn;
    private App app;

    public void initialize() {
        super.initialize();
        App app = App.getApplicationInstance();

        UserManager userManager = new UserManager(app.getConn());
        ScrollsManager scrollsManager = new ScrollsManager(app.getConn());

        // Initialize columns and set cell value factories
        keyIDColumn.setCellValueFactory(new PropertyValueFactory<>("keyID"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        numberOfScrolls.setCellValueFactory(cellData -> {
            User user = cellData.getValue(); // Get the User object for this row
            int uploadedScrolls = 0; // Call your method to get the count
            try {
                uploadedScrolls = userManager.countUploadedScrolls(user);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleIntegerProperty(uploadedScrolls).asObject();
        });
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        editUserButtonColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    String keyId = getTableView().getItems().get(getIndex()).getKeyID();
                    User user = null;
                    try {
                        user = userManager.findUserByID(keyId);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    app.loadUserProfile(App.getApplicationInstance().getActiveUser(), user);
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
        changeAccountTypeButtonColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button changeAccountTypeButton = new Button();

            {
                changeAccountTypeButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (!App.getApplicationInstance().getActiveUser().getKeyID().equals(user.getKeyID())) {
                        try {
                            userManager.changeAccountType(user);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Cannot demote yourself!");
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
                    setGraphic(changeAccountTypeButton);

                    User user = getTableView().getItems().get(getIndex());
                    changeAccountTypeButton.setText(user.getAccountType().equals("Admin") ? "Demote" : "Promote");
                }
            }
        });

        // Load data into the TableView
        tableView.setItems(getUserData());

        try {
            totalDownloadStat.setText(String.valueOf(scrollsManager.getTotalNumberOfDownloads()));
        } catch (SQLException e) {
            totalDownloadStat.setText("ERROR");
        }
        try {
            totalUserStat.setText(String.valueOf(userManager.getTotalNumberOfUsers()));
        } catch (SQLException e) {
            totalUserStat.setText("ERROR");
        }
        try {
            totalScrollsStat.setText(String.valueOf(scrollsManager.getTotalNumberOfScrolls()));
        } catch (SQLException e) {
            totalScrollsStat.setText("ERROR");
        }
    }

    public void createNewUser() {
        App app = App.getApplicationInstance();
        app.loadRegisterPage(true);
    }

    private ObservableList<User> getUserData() {
        app = App.getApplicationInstance();

        Connection conn = app.getConn();
        UserManager userManager = new UserManager(app.getConn());
        ObservableList<User> observableList = null;
        try {
            observableList = FXCollections.observableArrayList(userManager.getAllUsers());
        } catch (SQLException e) {
            return null;
        }
        return observableList;
    }
}
