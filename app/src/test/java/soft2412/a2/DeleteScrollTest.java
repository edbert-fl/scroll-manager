package soft2412.a2;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.controller.ScrollDeleter;
import soft2412.a2.model.Scroll;
import soft2412.a2.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.io.File;
import java.sql.SQLException;

import static org.mockito.Mockito.when;
import static soft2412.a2.TestHelpers.*;

public class DeleteScrollTest {

    private ScrollsManager scrollsManager;

    private User testUser;

    private App app;

    @AfterAll
    public static void tearDownEnvironment() {
        TestHelpers.tearDownEnvironment();
    }

    @Test
    public void testDatabaseDelete(){
        Connection testConnection = TestHelpers.createTestDatabase("deleteScrolLTest1.db");

        app = mock(App.class);
        testConnection = TestHelpers.createTestDatabase("deleteScrollTest.db");
        ScrollsManager scrollsManager = new ScrollsManager(testConnection);
        ScrollDeleter scrollDeleter = new ScrollDeleter();
        scrollsManager = new ScrollsManager(testConnection);
        try {
            scrollsManager.deleteScroll(scroll1, user1);
            scrollsManager.getScrollsForAll();
        } catch (SQLException e) {
            assertTrue(false, e.getMessage());
        }
        File targetFile = new File("3_spell_to_create_rainbows.txt");
        Assertions.assertFalse(targetFile.exists(), "failed to delete testFile");

        deleteDatabase(testConnection);
    }

    @Test
    public void testDatabaseDeleteFile() throws SQLException, IOException {
        Connection testConnection = TestHelpers.createTestDatabase("deleteScrollTest2.db");
        ScrollsManager scrollsManager = new ScrollsManager(testConnection);
        ScrollDeleter scrollDeleter = new ScrollDeleter();

        String userHome = System.getProperty("user.home");
        File vsasFolder = new File(userHome, ".vsas");

        File targetFolder = new File(vsasFolder, scroll2.getFilename());

        scrollDeleter.deleteFileFromServer(scroll2, scrollsManager, user1);

        boolean exist = targetFolder.exists();
        Assertions.assertFalse(exist, "failed to delete file from server");

        deleteDatabase(testConnection);
    }
}