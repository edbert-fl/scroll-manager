package soft2412.a2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import soft2412.a2.App;
import soft2412.a2.ExcludeFromJacocoGeneratedReport;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.model.Scroll;
import soft2412.a2.model.User;
import soft2412.a2.utility.PopupWindowController;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class MainPageController extends PageController{
    public Label scrolloftheday;
    public TableColumn previewButtonColumn;
    private ObservableList<Scroll> scrollData;
    @FXML
    public AnchorPane mainContent;
    @FXML
    public Button userProfile;
    @FXML
    public Button adminDashboardButton;
    @FXML
    public TableColumn<Scroll, Void> downloadButtonColumn;
    @FXML
    public TableColumn<Scroll, String> scrollNameColumn;
    @FXML
    public TableColumn<Scroll, Void> deleteButtonColumn;
    @FXML
    public HBox navBar;
    @FXML
    public ComboBox<String> CategoryDropDown;
    @FXML
    public TextField searchBar;
    @FXML
    public Button searchButton;
    @FXML
    private TableView<Scroll> tableView;
    @FXML
    private TableColumn<Scroll, String> dateColumn;
    @FXML
    private TableColumn<Scroll, String> fileNameColumn;
    @FXML
    private TableColumn<Scroll, User> uploaderColumn;
    @FXML
    private TableColumn<Scroll, Integer> downloadsColumn;
    private App app;

    @Override
    public void initialize() {
        super.initialize();
        App app = App.getApplicationInstance();
        if (app.getActiveUser().getAccountType().equals("Admin")) {
            deleteButtonColumn.setVisible(true);
        } else {
            deleteButtonColumn.setVisible(false);
        }

        // Initialize columns and set cell value factories
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("uploadDate"));
        scrollNameColumn.setCellValueFactory(new PropertyValueFactory<>("scrollName"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        uploaderColumn.setCellValueFactory(new PropertyValueFactory<>("uploaderName"));
        downloadsColumn.setCellValueFactory(new PropertyValueFactory<>("downloads"));
        CategoryDropDown.setItems(getColumns());
        CategoryDropDown.setValue(getColumns().get(0));
        downloadButtonColumn.setCellFactory(param -> new TableCell<Scroll, Void>() {
            private final Button downloadButton = new Button("Download");
            {
                downloadButton.setOnAction(event -> {
                    int scrollID = getTableView().getItems().get(getIndex()).getScrollID();
                    downloadScroll(scrollID); // Implement this method
                    app.loadMainPage();
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
        previewButtonColumn.setCellFactory(param -> new TableCell<Scroll, Void>() {
            private final Button previewButton = new Button("Preview");
            {
                previewButton.setOnAction(event -> {
                    Scroll scroll = getTableView().getItems().get(getIndex());
                    String previewText = previewScroll(scroll);
                    PopupWindowController popupWindowController = new PopupWindowController(previewText, 600, 400);
                    popupWindowController.showAndWait();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(previewButton);
                }
            }
        });
        deleteButtonColumn.setCellFactory(param -> new TableCell<Scroll, Void>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setOnAction(event -> {
                    Scroll scroll = getTableView().getItems().get(getIndex());
                    ScrollsManager scrollsManager = new ScrollsManager(app.getConn());
                    try {
                        scrollsManager.deleteScroll(scroll, app.getActiveUser()); // Implement this method
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    app.loadMainPage();
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
        scrollData = getScrollData();
        Scroll dailyScroll = getDailyScroll();
        if (dailyScroll != null){
            scrolloftheday.setText(String.format("Scroll of the day: %s by %s", dailyScroll.getScrollName(), dailyScroll.getUploaderName()));
        }
        tableView.setItems(scrollData);
    }

    public String previewScroll(Scroll scroll) {
        File scrollFile = new File(System.getProperty("user.home"), String.format(".vsas/%d_%s", scroll.getScrollID(), scroll.getFilename()));

        try (Scanner scanner = new Scanner(scrollFile)) {
            StringBuilder preview = new StringBuilder();
            int characterCount = 0;
            int maxCharacters = 200;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                preview.append(line).append("\n");
                characterCount += line.length();

                if (characterCount >= maxCharacters) {
                    break;
                }
            }

            String previewText = preview.toString();
            return previewText;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ObservableList<Scroll> getScrollData() {
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

    //attempt to get the daily scroll
    private Scroll getDailyScroll(){
        app = App.getApplicationInstance();

        Connection conn = app.getConn();
        ScrollsManager scrollsManager = new ScrollsManager(conn);
        Scroll dailyScroll = null;
        try {
            dailyScroll = scrollsManager.getDailyScroll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (dailyScroll == null){
            scrolloftheday.setText("No Interesting Scroll for you today yet :( Try to share something with us !!!");
            return null;
        }
        else{
            return dailyScroll;
        }
    }
    private ObservableList<String> getColumns(){
        app = App.getApplicationInstance();
        Connection conn = app.getConn();
        ScrollsManager scrollsManager = new ScrollsManager(conn);
        ObservableList<String> observableList = null;
        try {
            observableList = FXCollections.observableArrayList(scrollsManager.getColumnNames());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return observableList;
    }

    @FXML
    private void updateTableOnSearch(ActionEvent event) {
        app = App.getApplicationInstance();
        String category = CategoryDropDown.getValue();
        String search = searchBar.getText();
        if (search.isEmpty()){
            scrollData = getScrollData();
            tableView.setItems(scrollData);
            return;
        }
        Connection conn = app.getConn();
        ScrollsManager scrollsManager = new ScrollsManager(conn);
        try {
            scrollData = FXCollections.observableArrayList(scrollsManager.getScrollsByCategory(category, search));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(scrollData);
    }
}
