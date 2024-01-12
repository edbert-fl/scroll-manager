package soft2412.a2.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import soft2412.a2.App;

import java.io.File;

public class FileChooserController extends Stage {
    public File chooseFileToUpload() {
        App app = App.getApplicationInstance();
        FileChooser fileChooser = new FileChooser();

        // Configure the file chooser (e.g., set initial directory, file extensions, etc.)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Binary Files", "*.txt")
        );

        return fileChooser.showOpenDialog(this);
    }

    public File chooseFolderToDownloadTo(String initialFilename) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialFileName(initialFilename);

        return fileChooser.showSaveDialog(this);
    }
}
