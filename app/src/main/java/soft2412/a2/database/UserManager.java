package soft2412.a2.database;

import soft2412.a2.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;

public class UserManager {

    private Connection conn;

    //set connection
    public UserManager(Connection conn){
        this.conn = conn;
    }

    // TODO: Implement this function.
    public User findUserByID(String keyID) throws SQLException {
        String email;
        String username;
        String firstName;
        String lastName;
        String accountType;
        String sql = String.format("SELECT email, username, keyID, firstName, lastName, accountType\n"+
                "FROM Users\n" +
                "WHERE (keyID = '%s')", keyID);
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            email = rs.getString("email");
            username = rs.getString("username");
            keyID = rs.getString("keyID");
            firstName = rs.getString("firstName");
            lastName = rs.getString("lastName");
            accountType = rs.getString("accountType");
        }
        if (username == null) {
            return null;
        }

        //return new user object with all the attributes
        return new User(keyID, username,
                email,
                firstName,
                lastName,
                accountType);
    };

    public String updatePassword(User user, String newPassword) throws SQLException {
        if (conn != null) {
            String sql = "UPDATE Users SET hashedPassword = ? WHERE keyID = ?";
            String newHashedPassword = User.hashPassword(newPassword, user.getSalt());

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newHashedPassword);
            pstmt.setString(2, user.getKeyID());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                pstmt.close();
                return newHashedPassword;
            }
        }
        return null;
    }

    public int countUploadedScrolls(User user) throws SQLException {
        int uploadedScrolls = 0;

        Connection connection = conn;
        // Use a prepared statement
        String sql = "SELECT COUNT(scrollID) AS uploadedScrolls FROM Scrolls WHERE keyID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, user.getKeyID());

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            uploadedScrolls = resultSet.getInt("uploadedScrolls");
        }
        preparedStatement.close();
        return uploadedScrolls;
    }

    public int getTotalNumberOfUsers() throws SQLException {
        String sql = "SELECT COUNT(*) AS totalUsers\n" +
                "FROM Users;";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.getInt("totalUsers");
        }
    }

    public User findAccount(String username, String password) throws NoSuchAlgorithmException, SQLException {
        //attempt to store every attribute first
        String saltedPassword;
        String salt;
        String hashedPassword;
        String email;
        String phoneNumber;
        String keyID;
        String firstName;
        String lastName;
        String accountType;
        String sql = String.format("SELECT salt AS salt, HashedPassword AS hashedPassword, Email AS email, PhoneNumber AS phoneNumber, keyID, FirstName AS firstName, LastName AS lastName, AccountType AS accountType\n"+
                "FROM Users\n" +
                "WHERE (UserName = '%s')", username);
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            salt = rs.getString("salt");
            hashedPassword = rs.getString("hashedPassword");
            email = rs.getString("email");
            phoneNumber = rs.getString("phoneNumber");
            keyID = rs.getString("keyID");
            firstName = rs.getString("firstName");
            lastName = rs.getString("lastName");
            accountType = rs.getString("accountType");
        }

        //check if salted password = hashed password
        saltedPassword = hashPassword(password, salt);
        if (saltedPassword.equals(hashedPassword) != true){
            System.out.println("Invalid username or password.");
            return null;
        }

        //return new user object with all the attributes
        return new User(username,
                email,
                phoneNumber,
                keyID,
                firstName,
                lastName,
                salt,
                hashedPassword,
                accountType);
    }

    public User createNewUser(String username, String email, String phoneNumber, String keyID, String firstName, String lastName, String password, String accountType){
        //create temporary user to generate salt and HashedPassword
        User User = new User(username, email, phoneNumber, keyID, firstName, lastName, password, accountType);
        //format the input
        String sql = String.format("INSERT INTO Users (keyID, UserName, AccountType, Email, PhoneNumber, salt, HashedPassword, FirstName, LastName) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');", keyID, username,accountType,email,phoneNumber, User.getSalt(), User.getHashedPassword(), firstName, lastName);
        //store
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return User;
    }


    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE userName = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, username);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }
        return false;
    }

    public boolean keyIDExists(String keyID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE keyID = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, keyID);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }
        return false;
    }

    public ArrayList<User> getAllUsers() throws SQLException {
        String keyID;
        String username;
        String email;
        String firstname;
        String lastname;
        String accountType;
        ArrayList<User> userList = new ArrayList<User>();
        String sql = String.format("SELECT keyID, username, email, firstName, lastName, accountType\n"+
                "FROM Users;");

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()) {
                keyID = rs.getString("keyID");
                username = rs.getString("username");
                email = rs.getString("email");
                firstname = rs.getString("firstname");
                lastname = rs.getString("lastname");
                accountType = rs.getString("accountType");

                userList.add(new User(keyID, username, email, firstname, lastname, accountType));
            }
        }
        return userList;
    }

    private String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] passwordBytes = (password + salt).getBytes();
        byte[] hashedBytes = md.digest(passwordBytes);
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    public String changeAccountType(User user) throws SQLException {
        String newAccountType = user.getAccountType().equals("Admin") ? "Standard" : "Admin";

        String sql = "UPDATE Users SET accountType = ? WHERE keyID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newAccountType);
            pstmt.setString(2, user.getKeyID());

            pstmt.executeUpdate();
            return newAccountType;
        }
    }

    public void updateUser(User user, String newKeyID, String newFirstName, String newLastName, String newEmail, String newPhoneNumber) throws SQLException {
        String sql = "UPDATE Users SET keyID = ?, firstName = ?, lastName = ?, email = ?, phoneNumber = ? WHERE keyID = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newKeyID);
        pstmt.setString(2, newFirstName);
        pstmt.setString(3, newLastName);
        pstmt.setString(4, newEmail);
        pstmt.setString(5, newPhoneNumber);
        pstmt.setString(6, user.getKeyID());
        int rowsUpdated = pstmt.executeUpdate();
    }
}
