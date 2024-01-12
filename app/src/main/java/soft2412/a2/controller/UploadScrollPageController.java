package soft2412.a2.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.sqlite.SQLiteException;
import soft2412.a2.App;

import java.io.File;
import java.sql.SQLException;

public class UploadScrollPageController extends PageController {
    public TextField scrollNameField;
    public Label savedFile;
    public Label message;

    public void initialize() {
        super.initialize();
    }

    public void chooseFile() {
        App app = App.getApplicationInstance();

        FileChooserController fileChooserController = new FileChooserController();
        File file = fileChooserController.chooseFileToUpload();
        if (file != null) {
            savedFile.setText(file.getName());
            message.setText("");
            app.setCachedFile(file);
        } else {
            message.setText("Please choose a file.");
            message.getStyleClass().add("error-message");
        }
    }

    public void uploadChosenFile() {
        App app = App.getApplicationInstance();
        File file = app.getCachedFile();
        String scrollName = scrollNameField.getText();

        if (file == null) {
            message.setText("Please choose a file.");
            message.getStyleClass().add("error-message");
        } else if (scrollName == null || scrollName.equals("")) {
            message.setText("Please input a scroll name.");
            message.getStyleClass().add("error-message");
        } else {
            FileUploader fileUploader = new FileUploader();
            try {
                fileUploader.uploadFileToServer(app.getConn(), app.getActiveUser(), scrollName, file);
                app.loadMainPage();
            } catch (SQLException e) {
                message.setText("File name already exists!");
                message.getStyleClass().add("error-message");
            }
        }
    }

}
