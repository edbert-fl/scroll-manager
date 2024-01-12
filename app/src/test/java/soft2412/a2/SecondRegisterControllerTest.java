package soft2412.a2;
import static org.junit.jupiter.api.Assertions.*;
import soft2412.a2.controller.SecondRegisterController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class SecondRegisterControllerTest {
    private SecondRegisterController controller;
    @BeforeEach
    public void setup(){
        controller = new SecondRegisterController();
    }
    @Test
    public void testValidEmails() {
        assertTrue(controller.isValidEmail("example@email.com"));
        assertTrue(controller.isValidEmail("user.name+tag@example.co.uk"));
    }
    @Test
    public void testInvalidEmails() {
        assertFalse(controller.isValidEmail("exampleemail.com"));
        assertFalse(controller.isValidEmail("example@.com"));
        assertFalse(controller.isValidEmail("example@com"));
    }
    @Test
    public void testValidNames() {
        assertTrue(controller.isValidName("Johnny"));
        assertTrue(controller.isValidName("Anne-Marie"));
        assertTrue(controller.isValidName("O'Neal"));
    }
    @Test
    public void testInvalidNames() {
        assertFalse(controller.isValidName("John123"));
        assertFalse(controller.isValidName("John!"));
        assertFalse(controller.isValidName("12345"));
    }
    @Test
    public void testValidAustralianNumbers() {
        assertTrue(controller.isValidAustralianPhoneNumber("(02) 1234 5678"));
        assertTrue(controller.isValidAustralianPhoneNumber("0412 345 678"));
        assertTrue(controller.isValidAustralianPhoneNumber("0488805335"));
    }

    @Test
    public void testInvalidAustralianNumbers() {
        assertFalse(controller.isValidAustralianPhoneNumber("02 1234 5678"));
        assertFalse(controller.isValidAustralianPhoneNumber("(02) 1234-5678"));
        assertFalse(controller.isValidAustralianPhoneNumber("(02) 123 4567"));
        assertFalse(controller.isValidAustralianPhoneNumber("(02) 12345 6789"));
    }

}
