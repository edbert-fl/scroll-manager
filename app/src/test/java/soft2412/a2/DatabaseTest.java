package soft2412.a2;

import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import soft2412.a2.database.Database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static soft2412.a2.TestHelpers.*;

class DatabaseTest {
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private static Database testDatabase;
    private static Connection testConnection;

    @BeforeEach
    public void setUp() {
        deleteDatabase(testConnection);

        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outputStreamCaptor));

        testConnection = TestHelpers.createTestDatabase("databaseTest.db");
    }

    @AfterAll
    public static void tearDownEnvironment() {
        TestHelpers.tearDownEnvironment();
    }

    @Test
    void testConnect() {
        Assertions.assertNotNull(testConnection, "Connection should not be null when connecting to an existing database");
    }

    @Test
    void testCreateTables() throws SQLException {
        deleteDatabase(testConnection);
        Database testDatabase = null;
        try {
            testDatabase = new Database(TEST_DATABASE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        testConnection = testDatabase.getConn();

        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Users");
            stmt.execute("DROP TABLE IF EXISTS Scrolls");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        testDatabase.createTables();
        testConnection = testDatabase.getConn();
        boolean usersExists = false;
        boolean scrollsExists = false;
        try {
            DatabaseMetaData meta = testConnection.getMetaData();
            ResultSet scrollsTables = meta.getTables(null, null, "Scrolls", null);
            ResultSet usersTables = meta.getTables(null, null, "Users", null);
            usersExists = usersTables.next();
            scrollsExists = scrollsTables.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(usersExists, "Users table should exist after creation");
        Assertions.assertTrue(usersExists, "Scrolls table should exist after creation");
    }
}