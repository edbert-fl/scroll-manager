package soft2412.a2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import soft2412.a2.App;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.model.Scroll;
import soft2412.a2.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class GuestUserController extends PageController{

    @FXML
    public TableColumn downloadButtonColumn;
    @FXML
    private TableView<Scroll> tableView;
    @FXML
    private TableColumn<Scroll, LocalDateTime> dateColumn;
    @FXML
    private TableColumn<Scroll, String> fileNameColumn;
    @FXML
    private TableColumn<Scroll, User> uploaderColumn;
    @FXML
    private TableColumn<Scroll, Integer> downloadsColumn;

    @Override
    public void initialize() {
        this.app = App.getApplicationInstance();

        // Initialize columns and set cell value factories
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("uploadDate"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        uploaderColumn.setCellValueFactory(new PropertyValueFactory<>("uploaderName"));
        downloadsColumn.setCellValueFactory(new PropertyValueFactory<>("downloads"));

        // Load data into the TableView
        tableView.setItems(getScrollData());
        userProfile.setText("Guest");
    }

    protected ObservableList<Scroll> getScrollData() {
        app = App.getApplicationInstance();

        Connection conn = app.getConn();
        ScrollsManager scrollsManager = new ScrollsManager(conn);
        ObservableList<Scroll> observableList = null;
        try {
            observableList = FXCollections.observableArrayList(scrollsManager.getScrollsForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return observableList;
    }

    public void login() {
        app.loadLoginPage();
    }
}
