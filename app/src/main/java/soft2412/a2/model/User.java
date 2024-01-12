package soft2412.a2.model;

import soft2412.a2.database.UserManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
    private String username;
    private String hashedPassword;
    private String salt;
    private String accountType;
    private String email;
    private String phoneNumber;
    private String keyID;
    private String firstName;
    private String lastName;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z-' ]+$");
    private static final Pattern AUSTRALIAN_PHONE_PATTERN = Pattern.compile("^(\\(0[0-9]\\) [0-9]{4} [0-9]{4}|04[0-9]{2} [0-9]{3} [0-9]{3}|04[0-9]{8})$");


    /**
     * Constructs a new User with the provided username, password, and account type.
     *
     * @param username    The user's unique username.
     * @param email       The user's email.
     * @param phoneNumber The user's phone number.
     * @param idKey       The user's unique id key.
     * @param firstName   The user's first name.
     * @param lastName    The user's last name.
     * @param password    The user's un-hashed password.
     * @param accountType The account type, which must be "Guest" or "Admin".
     */
    public User(String username, String email, String phoneNumber, String keyID, String firstName, String lastName, String password, String accountType) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salt = generateSalt();
        this.hashedPassword = hashPassword(password, salt);
        if (isValidEmail(email)) {
            this.email = email;
            this.keyID = keyID;
            if (isValidAustralianPhoneNumber(phoneNumber)) {
                this.phoneNumber = phoneNumber;
                if (isValidAccountType(accountType)) {
                    this.accountType = accountType;
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Invalid account type, account must be Guest or Admin");
    }

    /**
     * Constructor for UserManager.findUser()
     *
     * @param username
     * @param email
     * @param phoneNumber
     * @param keyID
     * @param firstName
     * @param lastName
     * @param salt
     * @param hashedPassword
     * @param accountType
     */
    public User(String username, String email, String phoneNumber, String keyID, String firstName, String lastName, String salt, String hashedPassword, String accountType) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.keyID = keyID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salt = salt;
        this.hashedPassword = hashedPassword;
        this.accountType = accountType;
    }

    /**
     * Basic User constructor.
     *
     * @param keyID
     * @param username
     */
    public User(String username, String keyID) {
        this.username = username;
        this.keyID = keyID;
    }

    public User(String keyID, String username, String email, String firstName, String lastName, String accountType) {
        this.keyID = keyID;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountType = accountType;
    }

    public boolean isValidEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
    //phone number validation
    public boolean isValidAustralianPhoneNumber(String phoneNumber) {
        Matcher matcher = AUSTRALIAN_PHONE_PATTERN.matcher(phoneNumber);
        return matcher.matches();
    }

    /**
     * Checks if the provided account type is either "Guest" or "Admin".
     *
     * @param type The account type to be checked.
     * @return `true` if the account type is valid; otherwise, `false`.
     */
    public boolean isValidAccountType(String type) {
        return type.equals("Admin") || type.equals("Guest") || type.equals("Standard");
    }

    /**
     * Generates a random salt.
     *
     * @return The generated salt.
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * Hashes the provided password using SHA-256 and the stored salt.
     *
     * @param password The plaintext password to be hashed.
     * @param salt     The salt to be used for hashing.
     * @return The Base64-encoded hashed password.
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] passwordBytes = (password + salt).getBytes();
            byte[] hashedBytes = md.digest(passwordBytes);
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed.");
        }
    }

    /**
     * Returns the user's account type.
     *
     * @return accountType, a String that is either "Guest", "Admin" or "Standard".
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * Checks if the provided password matches the user's hashed password.
     *
     * @param pass The un-hashed password to be checked.
     * @return `true` if the password matches; otherwise, `false`.
     */
    public boolean checkLogin(String pass) {
        if (hashPassword(pass, salt).equals(hashedPassword)) {
            return true;
        }
        return false;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public String getKeyID() {
        return keyID;
    }

    public String getPhoneNumber() { return this.phoneNumber; }
}
