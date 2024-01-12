package soft2412.a2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteException;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.model.Scroll;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static soft2412.a2.TestHelpers.*;

public class ScrollsManagerTest {
    Connection testConnection;
    ScrollsManager scrollsManager;
    @BeforeEach
    public void setupEnvironment() {
        deleteDatabase(testConnection);

        testConnection = TestHelpers.createTestDatabase("scrollsManagerTest.db");
        scrollsManager = new ScrollsManager(testConnection);

        String userHome = System.getProperty("user.home");
        File downloadsFolder = new File(userHome, ".vsas/downloads_go_here");
        if (!downloadsFolder.exists() || !downloadsFolder.isDirectory()) {
            try {
                Files.createDirectories(downloadsFolder.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            File[] files = downloadsFolder.listFiles();
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
    public void testGetTotalNumberOfScrolls() throws SQLException {
        Connection getTotalNumberOfScrollsConnection = TestHelpers.createTestDatabase("getTotalNumberOfScrollsTest.db");
        scrollsManager = new ScrollsManager(getTotalNumberOfScrollsConnection);

        int result = scrollsManager.getTotalNumberOfScrolls();
        deleteDatabase(getTotalNumberOfScrollsConnection);
        assertEquals(2, result);
    }

    @Test
    public void testGetTotalNumberOfDownloads() throws SQLException {
        Connection getTotalNumberOfDownloadsConnection = TestHelpers.createTestDatabase("getTotalNumberOfDownloadsTest.db");
        scrollsManager = new ScrollsManager(getTotalNumberOfDownloadsConnection);

        int result = scrollsManager.getTotalNumberOfDownloads();
        deleteDatabase(getTotalNumberOfDownloadsConnection);
        assertEquals(32, result);
    }

    @Test
    public void testGetDailyScrollEmpty() throws SQLException {
        assertTrue(scrollsManager.getDailyScroll()!=null);
    }

    @Test
    public void testGetDailyScrollNumber() throws SQLException {
        assertNotNull(scrollsManager.getDailyScroll());
    }

    @Test
    public void testGetScrollsByCategory() throws SQLException {
        assertEquals(scroll1.getScrollName(), scrollsManager.getScrollsByCategory("scrollName", "Rainbows").getFirst().getScrollName());
    }

    @Test
    public void testGetColumnNames() throws SQLException {
        assertEquals("scrollName", scrollsManager.getColumnNames().getFirst());
        assertEquals("filename", scrollsManager.getColumnNames().getLast());
    }

    @Test
    public void testGetScrollsForUser() throws SQLException {
        assertNotNull(scrollsManager.getScrollsForUser(user1));
    }

    @Test
    public void testUpdateScrollName() throws SQLException {
        Connection updateScrollNameConnection = TestHelpers.createTestDatabase("updateScrollNameConnectionTest.db");
        scrollsManager = new ScrollsManager(updateScrollNameConnection);
        try {
            scrollsManager.updateScrollName(scroll1, "Rainbow Magic");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        Scroll result = scrollsManager.getScrollByScrollName("Rainbow Magic");
        deleteDatabase(updateScrollNameConnection);
        assertNotNull(result);
    }

    @Test
    public void testUpdateScrollNameNull() throws SQLException {
        Connection updateScrollNameConnection = TestHelpers.createTestDatabase("updateScrollNameConnectionTest.db");
        scrollsManager = new ScrollsManager(updateScrollNameConnection);
        try {
            scrollsManager.updateScrollName(new Scroll(1, "Some Days", "Calendar", "calendar.txt", user2, 0), "Rainbow Magic");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        Scroll result = scrollsManager.getScrollByScrollName("Calendar");
        deleteDatabase(updateScrollNameConnection);
        assertNull(result);
    }

    @Test
    public void testGetScrollByName() throws SQLException {
        assertEquals("spell_to_create_rainbows.txt", scrollsManager.getScrollByScrollName("Rainbows").getFilename());
    }

    @Test
    public void testGetScrollByNameNullExample() throws SQLException {
        assertNull(scrollsManager.getScrollByScrollName("RainbowStars"));
    }

    @Test
    public void testDeleteScrollAdmin() throws SQLException {
        Connection deleteScrollAdminConnection = TestHelpers.createTestDatabase("deleteScrollAdminTest.db");
        scrollsManager = new ScrollsManager(deleteScrollAdminConnection);

        assertNotNull(scrollsManager.deleteScroll(scroll2, user3));
    }
}
