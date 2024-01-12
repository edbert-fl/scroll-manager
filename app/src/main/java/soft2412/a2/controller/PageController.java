package soft2412.a2.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import soft2412.a2.App;
import soft2412.a2.model.Scroll;
import soft2412.a2.database.ScrollsManager;

import javafx.fxml.FXML;

import java.sql.SQLException;

public abstract class PageController {
    App app;
    public Button userProfile;
    @FXML
    public Button adminDashboardButton;
    @FXML
    public Label userProfileRole;

    public void initialize() {
        app = App.getApplicationInstance();

        if (app.getActiveUser().getAccountType().equals("Admin")) {
            adminDashboardButton.setVisible(true);
        }

        userProfile.setText(app.getActiveUser().getUsername());
        userProfileRole.setText(app.getActiveUser().getAccountType());
    }

    public void downloadScroll(int scrollID) {
        App app = App.getApplicationInstance();
        ScrollsManager scrollsManager = new ScrollsManager(app.getConn());
        Scroll scrollToBeDownloaded = null;
        try {
            scrollToBeDownloaded = scrollsManager.getScrollByID(scrollID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FileDownloader fileDownloader = new FileDownloader();
        fileDownloader.downloadFileFromServer(app.getConn(), scrollToBeDownloaded, new FileChooserController());
    }

    public void goToMainPage() {
        app = App.getApplicationInstance();

        app.loadMainPage();
    }

    public void goToUserProfile() {
        app = App.getApplicationInstance();

        app.loadUserProfile();
    }

    public void logout() {
        app = App.getApplicationInstance();

        app.loadLoginPage();
    }

    public void goToAdminDashboard() {
        app = App.getApplicationInstance();

        app.loadAdminDashboard();
    }

    public void goToUploadScrollPage() {
        app = App.getApplicationInstance();

        app.loadUploadPage();
    }

    public void loadEditScrollPage(Scroll scroll) {
        app = App.getApplicationInstance();

        app.loadEditScrollPage(scroll);
    }
}


