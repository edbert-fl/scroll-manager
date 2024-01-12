package soft2412.a2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteException;
import soft2412.a2.database.UserManager;
import soft2412.a2.model.User;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static soft2412.a2.TestHelpers.deleteDatabase;
import static soft2412.a2.TestHelpers.user1;

public class UserManagerTest {
    Connection testConnection;
    UserManager userManager;
    @BeforeEach
    public void setupEnvironment() {
        deleteDatabase(testConnection);
        testConnection = TestHelpers.createTestDatabase("userManagerTest.db");
        userManager = new UserManager(testConnection);
    }

    @AfterAll
    public static void tearDownEnvironment() {
        TestHelpers.tearDownEnvironment();
    }

    @Test
    public void testGetTotalNumberOfUsers() throws SQLException {
        Connection TotalNumberOfUsersConnection = TestHelpers.createTestDatabase("TotalNumberOfUsersConnection.db");
        userManager = new UserManager(TotalNumberOfUsersConnection);
        int totalUsers = userManager.getTotalNumberOfUsers();

        deleteDatabase(TotalNumberOfUsersConnection);
        assertEquals(3, totalUsers);
    }

    @Test
    public void updatePasswordNullConnection() throws SQLException {
        userManager = new UserManager(null);
        assertNull(userManager.updatePassword(user1, "NewPassword"));
    }

    @Test
    public void updateWrongUserPassword() throws SQLException {
        userManager = new UserManager(null);
        assertNull(userManager.updatePassword(new User("hey", "heyo"), "NewPassword"));
    }

    @Test
    public void testFindUserByID() throws SQLException {
        Connection updatePasswordConnection = TestHelpers.createTestDatabase("userManagerUpdatePasswordTest.db");
        userManager = new UserManager(updatePasswordConnection);
        assertEquals("John", userManager.findUserByID("UserOne").getFirstName());
    }

    @Test
    public void testUpdatePassword() throws SQLException {
        Connection updatePasswordConnection = TestHelpers.createTestDatabase("userManagerUpdatePasswordTest.db");
        userManager = new UserManager(updatePasswordConnection);

        assertTrue(user1.checkLogin("1234567890"));
        String newHashedPassword = userManager.updatePassword(user1, "a");

        deleteDatabase(updatePasswordConnection);
        assertEquals(newHashedPassword, User.hashPassword("a", user1.getSalt()));
    }

    @Test
    public void testFindAccount() throws SQLException, NoSuchAlgorithmException {
        Connection findAccountConnection = TestHelpers.createTestDatabase("userManagerFindAccountTest.db");
        userManager = new UserManager(findAccountConnection);

        User foundUser = userManager.findAccount("user1", "1234567890");
        deleteDatabase(findAccountConnection);
        assertNotNull(foundUser);
        assertEquals("John", foundUser.getFirstName());
    }

    @Test
    public void testFindAccountWrongPassword() throws SQLException, NoSuchAlgorithmException {
        Connection findAccountConnection = TestHelpers.createTestDatabase("userManagerFindAccountTest.db");
        userManager = new UserManager(findAccountConnection);

        User foundUser = userManager.findAccount("user1", "12345683354");
        deleteDatabase(findAccountConnection);
        assertNull(foundUser);
    }

    @Test
    public void testGetUploadCount() throws SQLException {
        Connection getUploadCountConnection = TestHelpers.createTestDatabase("getUploadCountTest.db");
        userManager = new UserManager(getUploadCountConnection);

        userManager.countUploadedScrolls(user1);
        deleteDatabase(getUploadCountConnection);
    }

    @Test
    public void testCreateNewUser() throws SQLException {
        Connection testCreateNewUserConnection = TestHelpers.createTestDatabase("testCreateNewUserTest.db");
        userManager = new UserManager(testCreateNewUserConnection);
        userManager.createNewUser("Penguin",
                "penguin@example.com",
                "0412345678",
                "penguin",
                "Arctic",
                "Penguin",
                "password",
                "Standard");

        assertNotNull(userManager.findUserByID("penguin"));
        deleteDatabase(testCreateNewUserConnection);
    }

    @Test
    public void testUsernameExists() throws SQLException {
        assertTrue(userManager.usernameExists("user1"));
    }

    @Test
    public void testUsernameDoesNotExist() throws SQLException {
        assertFalse(userManager.usernameExists("Banananana"));
    }

    @Test
    public void testKeyIDExists() throws SQLException {
        assertTrue(userManager.keyIDExists("UserTwo"));
    }

    @Test
    public void testKeyIDDoesNotExists() throws SQLException {
        assertFalse(userManager.keyIDExists("Banananana"));
    }

    @Test
    public void testGetAllUsers() throws SQLException {
        assertTrue(userManager.getAllUsers().size() >= 2);
    }

    @Test
    public void testChangeAccountType() throws SQLException {
        Connection changeAccountConnection = TestHelpers.createTestDatabase("changeAccountTypeTest.db");
        userManager = new UserManager(changeAccountConnection);

        assertEquals("Standard", user1.getAccountType());
        String newAccountType = userManager.changeAccountType(user1);
        assertEquals("Admin", newAccountType);
        deleteDatabase(changeAccountConnection);
    }

    @Test
    public void testUpdateUser() throws SQLException {
        Connection updateUserConnection = TestHelpers.createTestDatabase("updateUserConnection.db");
        userManager = new UserManager(updateUserConnection);
        try {
            userManager.updateUser(user1, user1.getKeyID(), "Josh", "Adams", user1.getEmail(), user1.getPhoneNumber());
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        assertEquals("Josh", userManager.findUserByID("UserOne").getFirstName());
        assertEquals("Adams", userManager.findUserByID("UserOne").getLastName());
        deleteDatabase(updateUserConnection);
    }
}
