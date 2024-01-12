package soft2412.a2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.model.Scroll;
import soft2412.a2.utility.PopupWindowController;

import java.io.*;
import java.sql.SQLException;
import java.util.Scanner;

public class EditScrollController extends PageController {
    @FXML
    public Label scrollName;
    @FXML
    public TextField scrollNameField;
    @FXML
    public Label messageLabel;
    @FXML
    private TextArea textArea;
    private Scroll scroll;

    @Override
    public void initialize() {
        super.initialize();
        if (scroll != null) {
            scrollName.setText(String.format("Editing %s", scroll.getScrollName()));
            String fileContent = "";
            try {
                File scrollFile = new File(System.getProperty("user.home"), String.format(".vsas/%d_%s", scroll.getScrollID(), scroll.getFilename()));
                Scanner myReader = new Scanner(scrollFile);
                while (myReader.hasNextLine()) {
                    String fileLine = myReader.nextLine();
                    fileContent += fileLine;
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            scrollNameField.setText(scroll.getFilename());
            textArea.setText(fileContent);
        }
    }

    public void saveScroll() {
        String newScrollName = scrollNameField.getText();
        ScrollsManager scrollsManager = new ScrollsManager(app.getConn());

        try {
            scrollsManager.updateScrollName(scroll, newScrollName);

            String newContent = textArea.getText();

            File scrollFile = new File(System.getProperty("user.home"), String.format(".vsas/%d_%s", scroll.getScrollID(), scroll.getFilename()));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(scrollFile))) {
                writer.write(newContent);
                System.out.println("File content updated.");
                PopupWindowController popupWindowController = new PopupWindowController("Successfully edited scroll!", 300, 100);
                popupWindowController.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Scroll content could not be updated!");
                messageLabel.getStyleClass().add("error-message");
            }
        } catch (SQLException e) {
            messageLabel.setText("Scroll name already exists!");
            messageLabel.getStyleClass().add("error-message");
        }
    }

    public void initializeData(Scroll scroll) {
        this.scroll = scroll;
    }
}
