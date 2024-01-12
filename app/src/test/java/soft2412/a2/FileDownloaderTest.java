package soft2412.a2;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import soft2412.a2.controller.FileChooserController;
import soft2412.a2.controller.FileDownloader;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.model.Scroll;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.when;
import static soft2412.a2.TestHelpers.*;

public class FileDownloaderTest {
    Connection testConnection;
    @BeforeEach
    public void setupEnvironment() {
        deleteDatabase(testConnection);

        testConnection = TestHelpers.createTestDatabase("fileDownloaderTest.db");

        if (!DOWNLOADS_FOLDER.exists() || !DOWNLOADS_FOLDER.isDirectory()) {
            try {
                Files.createDirectories(DOWNLOADS_FOLDER.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            File[] files = DOWNLOADS_FOLDER.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    @AfterAll
    public static void tearDownEnvironment() {
        TestHelpers.tearDownEnvironment();
    }

    @Test
    public void testDownloadFileFromServer() throws IOException, SQLException {
        Connection downloadFileFromServerConnection = TestHelpers.createTestDatabase("downloadFileFromServerTest.db");
        FileChooserController fileChooserController = Mockito.mock(FileChooserController.class);
        FileDownloader fileDownloader = new FileDownloader();

        String expectedFilename = "spell_to_create_rainbows.txt";

        // Mock the chooseFolderToDownloadTo function to return a File
        File sourceFile = new File(VSAS_FOLDER, "1_spell_to_create_rainbows.txt");
        File targetLocation = new File(VSAS_FOLDER, "downloads_go_here/spell_to_create_rainbows.txt");
        when(fileChooserController.chooseFolderToDownloadTo(expectedFilename))
                .thenReturn(targetLocation);

        ScrollsManager scrollsManager = new ScrollsManager(downloadFileFromServerConnection);

        int status = fileDownloader.downloadFileFromServer(downloadFileFromServerConnection, scrollsManager.getScrollByID(1), fileChooserController);

        Path sourcePath = sourceFile.toPath();
        Path targetPath = targetLocation.toPath();

        Assertions.assertEquals(0, status, "File does not exist at the target path.");
        deleteDatabase(downloadFileFromServerConnection);
    }

    @Test
    public void testUserDidNotChooseFolderToDownloadTo() throws IOException, SQLException {
        FileChooserController fileChooserController = Mockito.mock(FileChooserController.class);
        FileDownloader fileDownloader = new FileDownloader();

        String expectedFilename = "spell_to_create_rainbows.txt";

        // Mock the chooseFolderToDownloadTo function to return a File
        File sourceFile = new File(VSAS_FOLDER, "1_spell_to_create_rainbows.txt");
        File targetLocation = new File(VSAS_FOLDER, "downloads_go_here/spell_to_create_rainbows.txt");
        when(fileChooserController.chooseFolderToDownloadTo(expectedFilename))
                .thenReturn(null);

        ScrollsManager scrollsManager = new ScrollsManager(testConnection);

        int status = fileDownloader.downloadFileFromServer(testConnection, scrollsManager.getScrollByID(1), fileChooserController);

        Path sourcePath = sourceFile.toPath();
        Path targetPath = targetLocation.toPath();

        Assertions.assertEquals(1, status, "File does not exist at the target path.");
    }

    @Test
    public void testInvalidScrollToDownload() throws IOException, SQLException {
        FileChooserController fileChooserController = Mockito.mock(FileChooserController.class);
        FileDownloader fileDownloader = new FileDownloader();

        String expectedFilename = "spell_to_create_rainbows.txt";

        // Mock the chooseFolderToDownloadTo function to return a File
        File sourceFile = new File(VSAS_FOLDER, "1_spell_to_create_rainbows.txt");
        File targetLocation = new File(VSAS_FOLDER, "downloads_go_here/spell_to_create_rainbows.txt");
        when(fileChooserController.chooseFolderToDownloadTo(expectedFilename))
                .thenReturn(sourceFile);

        ScrollsManager scrollsManager = new ScrollsManager(testConnection);

        int status = fileDownloader.downloadFileFromServer(testConnection, scrollsManager.getScrollByID(10), fileChooserController);

        Path sourcePath = sourceFile.toPath();
        Path targetPath = targetLocation.toPath();

        Assertions.assertEquals(1, status, "File does not exist at the target path.");
    }

    @Test
    public void testNoScrollToDownload() throws IOException, SQLException {
        Connection noScrollToDownloadConnection = TestHelpers.createTestDatabase("noScrollToDownloadTest.db");
        FileChooserController fileChooserController = Mockito.mock(FileChooserController.class);
        FileDownloader fileDownloader = new FileDownloader();

        String expectedFilename = "spell_to_create_rainbows.txt";

        // Mock the chooseFolderToDownloadTo function to return a File
        File sourceFile = new File(VSAS_FOLDER, "1_spell_to_create_rainbows.txt");
        File targetLocation = new File(VSAS_FOLDER, "downloads_go_here/spell_to_create_rainbows.txt");
        when(fileChooserController.chooseFolderToDownloadTo(expectedFilename))
                .thenReturn(sourceFile);

        ScrollsManager scrollsManager = new ScrollsManager(noScrollToDownloadConnection);

        int status = fileDownloader.downloadFileFromServer(noScrollToDownloadConnection, new Scroll(8, "today", "todayScroll.txt", null, user2, 0), fileChooserController);

        Path sourcePath = sourceFile.toPath();
        Path targetPath = targetLocation.toPath();

        Assertions.assertEquals(1, status, "File does not exist at the target path.");
        deleteDatabase(noScrollToDownloadConnection);
    }
}
