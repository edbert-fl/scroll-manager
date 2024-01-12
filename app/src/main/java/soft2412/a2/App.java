package soft2412.a2;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import soft2412.a2.controller.*;
import soft2412.a2.database.Database;
import soft2412.a2.database.UserManager;
import soft2412.a2.model.Scroll;
import soft2412.a2.model.User;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class App extends Application {
    private Stage primaryStage;
    private Scene scene;
    private Connection conn;
    private User activeUser;
    private File cachedFile;
    private static App applicationInstance;
    public static void main(String[] args) {
        launch(args);
    }

    public static App getApplicationInstance() {
        return applicationInstance;
    }

    @Override
    public void init() {
        applicationInstance = this;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Database database = null;
        try {
            database = new Database("appdata.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conn = database.getConn();

        UserManager userManager = new UserManager(conn);
        User adminUser = null;
        try {
            adminUser = userManager.findUserByID("ADMIN");
        } catch (SQLException e) {}
        if (adminUser == null) {
            userManager.createNewUser("Elephant",
                    "elephant@gmail.com",
                    "0412345678",
                    "ADMIN",
                    "Elephant",
                    "Gray",
                    "password",
                    "Admin");
        }
        File vsasFolder = new File(System.getProperty("user.home"), ".vsas");

        // Create the .vsas folder if it doesn't exist
        if (!vsasFolder.exists()) {
            if (vsasFolder.mkdir()) {
                System.out.println(".vsas folder created in the user's home directory.");
            } else {
                System.err.println("Failed to create .vsas folder.");
                return;
            }
        }

        this.loadLoginPage();
    }

    public void login(User user) {
        this.activeUser = user;
        this.loadMainPage();
    }

    public void loadAdminDashboard() {
        try{
            java.net.URL adminDashboard = getClass().getResource("/fxml/AdminDashboard.fxml");
            if(adminDashboard == null){
                throw new NullPointerException("FXML file 'AdminDashboard.fxml' not found. Checked in: " + getClass().getResource("").getPath());
            }
            FXMLLoader loader = new FXMLLoader(adminDashboard);
            Parent root = loader.load();
            AdminPageController adminPageController = loader.getController();
            primaryStage.setTitle("Virtual Scroll Access System");
            primaryStage.setScene(new Scene(root, 1920, 1080));
            primaryStage.show();
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/css/admin-dashboard.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadLoginPage() {
        try {
            java.net.URL loginPage = getClass().getResource("/fxml/Login.fxml");
            if (loginPage == null) {
                throw new NullPointerException("FXML file 'Login.fxml' not found. Checked in: " + getClass().getResource("").getPath());
            }
            FXMLLoader loader = new FXMLLoader(loginPage);
            Parent root = loader.load();
            LoginController loginController = loader.getController();

            primaryStage.setTitle("Virtual Scroll Access System");
            primaryStage.setScene(new Scene(root, 1920, 1080));
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadRegisterPage() {
        loadRegisterPage(false);
    }
    public void loadRegisterPage(boolean isAdmin){
        try{
            java.net.URL registerPage = getClass().getResource("/fxml/Register.fxml");
            if(registerPage == null){
                throw new NullPointerException("FXML file 'Register.fxml' not found. Checked in: " + getClass().getResource("").getPath());
            }
            FXMLLoader loader = new FXMLLoader(registerPage);
            Parent root = loader.load();
            RegisterController registerController = loader.getController();
            registerController.initializeData(isAdmin);
            registerController.initialize();
            primaryStage.setTitle("Virtual Scroll Access System");
            primaryStage.setScene(new Scene(root, 1920, 1080));
            primaryStage.show();
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/css/register.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadSecondRegisterPage(boolean isAdmin, String username, String password, String idKey) {
        try{
            java.net.URL secondRegisterPage = getClass().getResource("/fxml/Register2.fxml");
            if(secondRegisterPage == null){
                throw new NullPointerException("FXML file 'Register2.fxml' not found. Checked in: " + getClass().getResource("").getPath());
            }
            FXMLLoader loader = new FXMLLoader(secondRegisterPage);
            Parent root = loader.load();
            SecondRegisterController secondRegisterController = loader.getController();
            secondRegisterController.initializeData(isAdmin, username, password, idKey);
            secondRegisterController.initialize();

            primaryStage.setTitle("Virtual Scroll Access System");
            primaryStage.setScene(new Scene(root, 1920, 1080));
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/css/register.css").toExternalForm());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadMainPage() {
        try {
            java.net.URL mainPage = getClass().getResource("/fxml/Main.fxml");
            if (mainPage == null) {
                throw new NullPointerException("FXML file 'Main.fxml' not found. Checked in: " + getClass().getResource("").getPath());
            }
            FXMLLoader loader = new FXMLLoader(mainPage);
            Parent root = loader.load();
            MainPageController mainPageController = loader.getController();
            primaryStage.setTitle("Virtual Scroll Access System");

            primaryStage.setScene(new Scene(root, 1920, 1080));
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/css/main-page.css").toExternalForm());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleGoToGuestPage(ActionEvent event){
        try {
            // Load the registration page
            java.net.URL guestPage = getClass().getResource("/fxml/Guest.fxml");
            if(guestPage == null){
                throw new NullPointerException("FXML file 'Guest.fxml' not found. Checked in: " + getClass().getResource("").getPath());
            }
            FXMLLoader loader = new FXMLLoader(guestPage);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            root.getStylesheets().add(getClass().getResource("/css/main-page.css").toExternalForm());
            stage.setScene(new Scene(root));
            stage.setTitle("Guest user");
            stage.setWidth(1920);
            stage.setHeight(1080);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String showEditPopup(String attribute, User user) {
        try {
            FXMLLoader editLoader = new FXMLLoader(getClass().getResource("/fxml/EditUserInfo.fxml"));
            Parent editRoot = editLoader.load();

            EditUserInfoController editController = editLoader.getController();

            Stage editPopupStage = new Stage();
            editPopupStage.initModality(Modality.APPLICATION_MODAL);
            editPopupStage.initOwner(primaryStage);

            editPopupStage.setTitle("Edit User Information");
            editPopupStage.setScene(new Scene(editRoot, 250, 120));
            editController.initializeData(editPopupStage, attribute, user);
            editController.initialize();

            // Show the edit popup
            editPopupStage.showAndWait();
            return editController.getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String showEditPasswordPopup(User user) {
        try {
            FXMLLoader editLoader = new FXMLLoader(getClass().getResource("/fxml/EditPassword.fxml"));
            Parent editRoot = editLoader.load();

            EditPasswordController editPasswordController = editLoader.getController();

            Stage editPopupStage = new Stage();
            editPopupStage.initModality(Modality.APPLICATION_MODAL);
            editPopupStage.initOwner(primaryStage);

            editPopupStage.setTitle("Edit Password");
            editPopupStage.setScene(new Scene(editRoot, 300, 150));
            editPasswordController.initializeData(editPopupStage, user);
            editPasswordController.initialize();

            // Show the edit popup
            editPopupStage.showAndWait();
            return editPasswordController.getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadUserProfile() {
        loadUserProfile(null, activeUser);
    }
    public void loadUserProfile(User admin, User user) {
        try {
            java.net.URL mainPage = getClass().getResource("/fxml/UserProfile.fxml");
            if (mainPage == null) {
                throw new NullPointerException("FXML file 'UserProfile.fxml' not found. Checked in: " + getClass().getResource("").getPath());
            }
            FXMLLoader loader = new FXMLLoader(mainPage);
            Parent root = loader.load();
            UserProfileController userProfileController = loader.getController();
            userProfileController.initializeData(admin, user);
            userProfileController.initialize();

            primaryStage.setTitle("Virtual Scroll Access System");
            primaryStage.setScene(new Scene(root, 1920, 1080));
            scene = primaryStage.getScene();
            scene.getStylesheets().add(getClass().getResource("/css/user-profile.css").toExternalForm());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUploadPage() {
        try {
            java.net.URL mainPage = getClass().getResource("/fxml/UploadScrollPage.fxml");
            if (mainPage == null) {
                throw new NullPointerException("FXML file 'UploadScrollPage.fxml' not found. Checked in: " + getClass().getResource("").getPath());
            }
            FXMLLoader loader = new FXMLLoader(mainPage);
            Parent root = loader.load();
            UploadScrollPageController uploadScrollPageController = loader.getController();

            primaryStage.setTitle("Virtual Scroll Access System");
            primaryStage.setScene(new Scene(root, 1920, 1080));
            scene = primaryStage.getScene();
            scene.getStylesheets().add(getClass().getResource("/css/upload-page.css").toExternalForm());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadEditScrollPage(Scroll scroll) {
        try {
            java.net.URL mainPage = getClass().getResource("/fxml/EditScrollPage.fxml");
            if (mainPage == null) {
                throw new NullPointerException("FXML file 'EditScrollPage.fxml' not found. Checked in: " + getClass().getResource("").getPath());
            }
            FXMLLoader loader = new FXMLLoader(mainPage);
            Parent root = loader.load();
            EditScrollController editScrollController = loader.getController();
            editScrollController.initializeData(scroll);
            editScrollController.initialize();

            primaryStage.setTitle("Virtual Scroll Access System");
            primaryStage.setScene(new Scene(root, 1920, 1080));
            scene = primaryStage.getScene();
            scene.getStylesheets().add(getClass().getResource("/css/edit-scroll-page.css").toExternalForm());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCachedFile(File cachedFile) { this.cachedFile = cachedFile; }
    public User getActiveUser() {
        return activeUser;
    }

    public Connection getConn() {
        return conn;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public File getCachedFile() { return cachedFile; }
}
