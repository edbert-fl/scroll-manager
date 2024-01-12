package soft2412.a2.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import soft2412.a2.App;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;

public class PreviewController {
    String userHome = System.getProperty("user.home");
    File vsasFolder = new File(userHome, ".vsas");

    /**
     *
     * @param scroll scroll to be previewed
     * @return string content of the binary file
     */
    public String ShowPreview(Scroll scroll){
        String result = "";
        //get file name of scroll
        // Changes the filename to be in the format <scrollID>_<filename>
        String TargetFileName = String.format("%d_%s", scroll.getScrollID(), scroll.getFilename());
        File target = new File(vsasFolder, TargetFileName);
        //get content from file
        try (FileInputStream fis = new FileInputStream(target)) {
            int content;
            while ((content = fis.read()) != -1) {
                //append character to string
                result = result + "" + ((char)content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
