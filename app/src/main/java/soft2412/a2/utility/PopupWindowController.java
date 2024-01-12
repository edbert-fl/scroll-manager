package soft2412.a2.utility;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class PopupWindowController {

    @FXML
    private Label messageLabel;

    private Stage popupStage;

    public PopupWindowController(String message, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PopupWindow.fxml"));
            loader.setController(this);
            GridPane popupPane = loader.load();
            popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(popupPane, width, height));
            messageLabel.setText(message);
            popupStage.getScene().getStylesheets().add(getClass().getResource("/css/PopupWindow.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        return messageLabel.getText();
    }

    public void showAndWait() {
        popupStage.showAndWait();
    }
}
