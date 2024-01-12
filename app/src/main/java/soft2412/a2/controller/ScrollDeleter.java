package soft2412.a2.controller;

import soft2412.a2.database.ScrollsManager;
import soft2412.a2.model.Scroll;
import soft2412.a2.model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class ScrollDeleter {
    private final String SERVER_NAME = ".vsas";

    public void deleteFileFromServer(Scroll scroll, ScrollsManager scrollsManager, User activeUser) throws SQLException, IOException {

        Scroll scrollToBeDeleted = null;
        scrollToBeDeleted = scrollsManager.deleteScroll(scroll, activeUser);

        if (scrollToBeDeleted != null) {

            String userHome = System.getProperty("user.home");
            File vsasFolder = new File(userHome, SERVER_NAME);

            // Changes the filename to be in the format <scrollID>_<filename>
            String TargetFileName = String.format("%d_%s", scrollToBeDeleted.getScrollID(), scrollToBeDeleted.getFilename());
            File targetFolder = new File(vsasFolder, TargetFileName);

            Path finalFilePath = targetFolder.toPath();
            Files.deleteIfExists(finalFilePath);

            System.out.println(finalFilePath.toString() + " was deleted");
        }
    }
}
