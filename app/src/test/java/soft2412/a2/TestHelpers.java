package soft2412.a2;

import org.junit.jupiter.api.AfterAll;
import soft2412.a2.database.Database;
import soft2412.a2.database.ScrollsManager;
import soft2412.a2.database.UserManager;
import soft2412.a2.model.Scroll;
import soft2412.a2.model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestHelpers {
    public static final String TEST_DATABASE_NAME = "testDatabase.db";
    public static final String USER_HOME = System.getProperty("user.home");
    public static final File VSAS_FOLDER = new File(USER_HOME, ".vsas");
    public static final File UPLOAD_FOLDER = new File(VSAS_FOLDER, "upload_from_here");
    public static final File DOWNLOADS_FOLDER = new File(VSAS_FOLDER, "downloads_go_here");
    public static User user1;
    public static User user2;
    public static User user3;
    public static Scroll scroll1;
    public static Scroll scroll2;

    public static void deleteDatabase(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String dbFilePath = "src/main/database/" + TEST_DATABASE_NAME;
        File dbFile = new File(dbFilePath);
        if (dbFile.exists()) {
            int maxAttempts = 3;
            int attempt = 0;
            boolean deleted = false;

            while (attempt < maxAttempts && !deleted) {
                if (dbFile.delete()) {
                    deleted = true;
                } else {
                    attempt++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!deleted) {
                System.err.println(String.format("Failed to delete test.db at %s", dbFile.getAbsolutePath()));
            }
        }
    }

    public static void insertSampleData(Connection conn) {

        if (!VSAS_FOLDER.exists()) {
            if (VSAS_FOLDER.mkdir()) {
                System.out.println(".vsas folder created in the user's home directory.");
            } else {
                System.err.println("Failed to create .vsas folder.");
                return;
            }
        }

        UserManager userManager = new UserManager(conn);

        user1 = userManager.createNewUser("user1", "user1@example.com", "0412345678", "UserOne", "John", "Doe", "1234567890", "Standard");
        user2 = userManager.createNewUser("user2", "user2@example.com", "0423456789", "UserTwo", "Adam", "Smith", "0987654321", "Standard");
        user3 = userManager.createNewUser("user3", "user3@example.com", "0421234589", "UserThree", "Polar", "Bear", "0987654321", "Admin");

        ScrollsManager scrollsManager = new ScrollsManager(conn);

        scroll1 = scrollsManager.uploadScroll("Rainbows","spell_to_create_rainbows.txt", user1, 18);
        scroll2 = scrollsManager.uploadScroll("Frog Controller", "spell_to_turn_people_into_frogs.txt", user1, 14);

        createBinaryTextFiles();
    }

    public static Connection createTestDatabase(String databaseName) {
        Database database = null;
        try {
            database = new Database(databaseName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        insertSampleData(database.getConn());
        return database.getConn();
    }

    public static void createBinaryTextFiles() {
        try {
            File file1 = new File(VSAS_FOLDER, "1_spell_to_create_rainbows.txt");
            Files.write(file1.toPath(), "10001000".getBytes());

            File file2 = new File(VSAS_FOLDER, "2_spell_to_turn_people_into_frogs.txt");
            Files.write(file2.toPath(), "01000100".getBytes());

            File file3 = new File(UPLOAD_FOLDER, "spell_to_bake_cookies.txt");
            Files.write(file3.toPath(), "10001000".getBytes());

            File file4 = new File(UPLOAD_FOLDER, "spell_to_clean_laundry.txt");
            Files.write(file4.toPath(), "01000100".getBytes());

            File file5 = new File(UPLOAD_FOLDER, "spell_to_water_plants.txt");
            Files.write(file5.toPath(), "00100010".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void tearDownEnvironment() {
        String folderPath = "src/main/database/";
        String excludeFile = "appData.db";

        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".db"));
            if (files != null) {
                for (File file : files) {
                    if (!file.getName().equals("appdata.db")) {
                        if (file.delete()) {
                            System.out.println("Deleted: " + file.getAbsolutePath());
                        } else {
                            System.err.println("Failed to delete: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }
}
