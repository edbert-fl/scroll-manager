package soft2412.a2.controller;

import javafx.stage.FileChooser;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.model.Scroll;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;

public class FileDownloader {

    private FileChooser fileChooser;
    public int downloadFileFromServer(Connection conn, Scroll scrollToDownload, FileChooserController fileChooserController) {
        if (scrollToDownload == null) {
            System.err.println("Invalid Scroll to download.");
            return 1;
        }

        String userHome = System.getProperty("user.home");
        String SERVER_NAME = ".vsas";
        File vsasFolder = new File(userHome, SERVER_NAME);
        String newTargetFileName = String.format("%d_%s", scrollToDownload.getScrollID(), scrollToDownload.getFilename());
        File sourceFile = new File(vsasFolder, newTargetFileName);

        if (!sourceFile.exists()) {
            System.err.println("File not found on the server.");
            return 1;
        }

        if (fileChooserController == null) {
            fileChooserController = new FileChooserController();
        }
        File targetDirectory = fileChooserController.chooseFolderToDownloadTo(scrollToDownload.getFilename());

        if (targetDirectory != null) {
            try {
                Path sourcePath = sourceFile.toPath();
                Path targetPath = targetDirectory.toPath();
                try {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File downloaded to: " + targetPath.toString());
                    ScrollsManager scrollsManager = new ScrollsManager(conn);
                    scrollsManager.increaseDownload(scrollToDownload);
                    return 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error downloading the file.");
                return 1;
            }
        } else {
            System.out.println("Download canceled by the user.");
            return 1;
        }
    }

    public void setFileChooser(FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }
}
