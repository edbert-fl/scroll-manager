package soft2412.a2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import soft2412.a2.controller.FileChooserController;
import soft2412.a2.controller.FileUploader;
import soft2412.a2.database.ScrollsManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import static soft2412.a2.TestHelpers.*;

public class FileUploaderTest {
    Connection testConnection;
    @BeforeEach
    public void setupEnvironment() {
        deleteDatabase(testConnection);

        if (!UPLOAD_FOLDER.exists() || !UPLOAD_FOLDER.isDirectory()) {
            try {
                Files.createDirectories(UPLOAD_FOLDER.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            File[] files = UPLOAD_FOLDER.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
            createBinaryTextFiles();
        }
        testConnection = TestHelpers.createTestDatabase("fleUploaderTest.db");
    }

    @AfterAll
    public static void tearDownEnvironment() {
        TestHelpers.tearDownEnvironment();
    }

    @Test
    public void testUploadFileToServer() throws IOException {
        FileChooserController fileChooserController = Mockito.mock(FileChooserController.class);
        App app = Mockito.mock(App.class);
        FileUploader fileUploader = new FileUploader();

        String expectedFilename = "spell_to_bake_cookies.txt";

        // Mock the chooseFolderToDownloadTo function to return a File
        File sourceFile = new File(UPLOAD_FOLDER, expectedFilename);
        File targetLocation = new File(VSAS_FOLDER, String.format("%d_%s", 3, expectedFilename));
        Mockito.when(fileChooserController.chooseFileToUpload())
                .thenReturn(sourceFile);

        ScrollsManager scrollsManager = new ScrollsManager(testConnection);
        int status = 1;
        try {
            status = fileUploader.uploadFileToServer(testConnection, user2, "Cookies!", sourceFile);
        } catch (SQLException e) {
            System.out.println("SQLException >>> TestHelpers.Java: insertSampleData(Connection conn): Could not insert scrolls.");
        }

        Path sourcePath = sourceFile.toPath();
        Path targetPath = targetLocation.toPath();

        Assertions.assertEquals(0, status, "File does not exist at the target path.");
    }

    @Test
    public void testUploadFileToServerNoFile() throws IOException {
        FileChooserController fileChooserController = Mockito.mock(FileChooserController.class);
        App app = Mockito.mock(App.class);
        FileUploader fileUploader = new FileUploader();

        String expectedFilename = "spell_to_bake_cookies.txt";

        // Mock the chooseFolderToDownloadTo function to return a File
        File sourceFile = new File(UPLOAD_FOLDER, expectedFilename);
        File targetLocation = new File(VSAS_FOLDER, String.format("%d_%s", 3, expectedFilename));
        Mockito.when(fileChooserController.chooseFileToUpload())
                .thenReturn(sourceFile);

        ScrollsManager scrollsManager = new ScrollsManager(testConnection);
        int status = 1;
        try {
            status = fileUploader.uploadFileToServer(testConnection, user2, "Cookies!", null);
        } catch (SQLException e) {
            System.out.println("SQLException >>> TestHelpers.Java: insertSampleData(Connection conn): Could not insert scrolls.");
        }

        Path sourcePath = sourceFile.toPath();
        Path targetPath = targetLocation.toPath();

        Assertions.assertEquals(1, status, "File exists at the target path.");
    }

    @Test
    public void testUploadFileToServerWithSameFilename() throws IOException, SQLException {
        Connection uploadFileToServerWithSameFilenameConnection = TestHelpers.createTestDatabase("uploadFileToServerWithSameFilenameTest.db");
        ScrollsManager scrollsManager = new ScrollsManager(uploadFileToServerWithSameFilenameConnection);

        FileChooserController fileChooserController = Mockito.mock(FileChooserController.class);
        FileUploader fileUploader = new FileUploader();

        String expectedFilename = "spell_to_bake_cookies.txt";

        // Mock the chooseFolderToDownloadTo function to return a File
        File sourceFile = new File(UPLOAD_FOLDER, expectedFilename);
        File targetLocation = new File(VSAS_FOLDER, String.format("%d_%s", 3, expectedFilename));

        Mockito.when(fileChooserController.chooseFileToUpload())
                .thenReturn(sourceFile)
                .thenReturn(sourceFile);

        int status = fileUploader.uploadFileToServer(uploadFileToServerWithSameFilenameConnection, user2, "Cookies!", sourceFile);

        Assertions.assertEquals(0, status, "File does not exist at the target path.");

        targetLocation = new File(VSAS_FOLDER, String.format("%d_%s", 4, expectedFilename));
        status = fileUploader.uploadFileToServer(uploadFileToServerWithSameFilenameConnection, user2, "Cookies2", sourceFile);

        Assertions.assertEquals(0, status, "File does not exist at the target path.");
        deleteDatabase(uploadFileToServerWithSameFilenameConnection);
    }
}