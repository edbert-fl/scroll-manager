package soft2412.a2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import soft2412.a2.database.UserManager;
import soft2412.a2.model.User;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static soft2412.a2.TestHelpers.user1;

public class UserTest {

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("PolarBear",
                "polarbear@example.com",
                "0412345678",
                "polarbear",
                "Polar",
                "Bear",
                "password",
                "Standard");
    }

    @AfterAll
    public static void tearDownEnvironment() {
        TestHelpers.tearDownEnvironment();
    }

    @Test
    public void testHashPassword() {
        String salt = "salt";
        String hashedPassword = User.hashPassword("password", salt);
        String hashedPassword2 = User.hashPassword("password", salt);
        assertEquals(hashedPassword, hashedPassword2);
    }

    @Test
    public void testCheckLoginPasswordMatches() {
        User accessUser = new User("PolarBear",
                "polarbear@example.com",
                "0412345678",
                "polarbear",
                "Polar",
                "Bear",
                user.getSalt(),
                user.getHashedPassword(),
                "Standard");

        assertTrue(accessUser.checkLogin("password"));
    }

    @Test
    public void testIsValidAccount() {
        Connection isValidAccountConnection = TestHelpers.createTestDatabase("isValidAccountTest.db");
        UserManager userManager = new UserManager(isValidAccountConnection);
        assertTrue(user1.isValidAccountType("Standard"));
        assertTrue(user1.isValidAccountType("Guest"));
        assertTrue(user1.isValidAccountType("Admin"));
        assertFalse(user1.isValidAccountType("Super Admin"));
    }

    @Test
    public void testBasicConstructor() {
        assertNotNull(new User("hey", "hey"));
    }

    @Test
    public void testCheckLoginPasswordDoesNotMatch() {
        User accessUser = new User("PolarBear",
                "polarbear@example.com",
                "0412345678",
                "polarbear",
                "Polar",
                "Bear",
                user.getHashedPassword(),
                user.getSalt(),
                "Standard");

        assertFalse(accessUser.checkLogin("wrongpassword"));
    }

    @Test
    public void testCheckAccountInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("PolarBear",
                    "polarbear",
                    "0412345678",
                    "polarbear",
                    "Polar",
                    "Bear",
                    "12345678",
                    "Standard");
        });
    }

    @Test
    public void testInvalidPhoneNumber() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("PolarBear",
                    "polarbear",
                    "0412345678123456789",
                    "polarbear",
                    "Polar",
                    "Bear",
                    "12345678",
                    "Standard");
        });
    }
}
