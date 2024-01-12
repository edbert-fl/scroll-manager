package soft2412.a2.controller;

import org.sqlite.SQLiteException;
import soft2412.a2.App;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.model.Scroll;
import soft2412.a2.model.User;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;

public class FileUploader {
    private final String SERVER_NAME = ".vsas";

    public int uploadFileToServer(Connection conn, User activeUser, String scrollName, File selectedFile) throws SQLException {
        ScrollsManager scrollsManager = new ScrollsManager(conn);

        Scroll scrollToBeUploaded = null;
        if (selectedFile != null){
            scrollToBeUploaded = scrollsManager.uploadScroll(scrollName, selectedFile.getName(), activeUser);
        }
        if (selectedFile != null && scrollToBeUploaded != null) {
            System.out.println("Selected file path: " + selectedFile.getAbsolutePath());

            String userHome = System.getProperty("user.home");
            File vsasFolder = new File(userHome, SERVER_NAME);

            // Changes the filename to be in the format <scrollID>_<filename>
            String newTargetFileName = String.format("%d_%s", scrollToBeUploaded.getScrollID(), scrollToBeUploaded.getFilename());
            File targetFolder = new File(vsasFolder, newTargetFileName);

            if (copyFileToFolder(selectedFile, targetFolder)) {
                System.out.println("File copied to: " + targetFolder.getAbsolutePath());
                return 0;
            }
        }
        return 1;
    }

    private boolean copyFileToFolder(File sourceFile, File targetFolder) {
        try {
            Path sourcePath = sourceFile.toPath();
            Path targetPath = targetFolder.toPath();
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
