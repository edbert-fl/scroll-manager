package soft2412.a2.database;

import soft2412.a2.model.Scroll;
import soft2412.a2.model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.lang.Math;
import java.util.Date;


public class ScrollsManager {
    private ArrayList<Scroll> scrollList;

    private Connection conn;



    /**
     * Setup our userDatabase
     * @param conn Existing connection to our database
     */
    public ScrollsManager(Connection conn) {
        this.conn = conn;
        this.scrollList = new ArrayList<>();
    }

    /**
     * Retrieve all scrolls
     */
    public ArrayList<Scroll> getScrollsForAll() throws SQLException {
        this.scrollList.clear();
        String sql = "SELECT uploadDate, scrollID, scrollName, filename, keyID, downloads\n" +
                "FROM Scrolls;";

        UserManager userManager = new UserManager(conn);

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // create a new table
            while (rs.next()) {
                String uploadDate = rs.getString("uploadDate");
                Integer scrollID = rs.getInt("scrollID");
                String scrollName = rs.getString("scrollName");
                String filename = rs.getString("filename");
                String keyID = rs.getString("keyID");
                Integer downloads = rs.getInt("downloads");
                scrollList.add(new Scroll(scrollID, uploadDate, scrollName, filename, userManager.findUserByID(keyID), downloads));
            }
        }

        return scrollList;
    }

    public ArrayList<String> getColumnNames() throws SQLException {
        ArrayList<String> colNames = new ArrayList<>();
        String sql = "SELECT scrollName, filename\n" +
                "FROM Scrolls;";

        UserManager userManager = new UserManager(conn);

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);) {
            ResultSetMetaData rsMetaData = rs.getMetaData();
            // create a new table
            int count = rsMetaData.getColumnCount();
            for(int i = 1; i<=count; i++) {
                colNames.add(rsMetaData.getColumnName(i));
            }
        }
        return colNames;
    }

    public ArrayList<Scroll> getScrollsByCategory(String category, String content) throws SQLException {
        this.scrollList.clear();
        content = "'%" + content + "%'";
        String sql = String.format("SELECT uploadDate, scrollID, scrollName, filename, keyID, downloads\n" +
                "FROM Scrolls\n" +
                "WHERE LOWER(%s) LIKE %s;", category, content);
        System.out.println(sql);

        UserManager userManager = new UserManager(conn);

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // create a new table
            while (rs.next()) {
                String uploadDate = rs.getString("uploadDate");
                Integer scrollID = rs.getInt("scrollID");
                String scrollName = rs.getString("scrollName");
                String filename = rs.getString("filename");
                String keyID = rs.getString("keyID");
                Integer downloads = rs.getInt("downloads");
                System.out.println(keyID);
                scrollList.add(new Scroll(scrollID, uploadDate, scrollName, filename, userManager.findUserByID(keyID), downloads));
            }
        }

        return scrollList;
    }

    public Scroll getDailyScroll() throws SQLException {
        String sql;
        //get date as long in the form of DDMMYYYY
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        String stringSeed = "" + year + month + day;
        int seed = Integer.parseInt(stringSeed);
        int theDay = 20231026;

        //processing of getting random scroll
        Scroll randomScroll;

        UserManager userManager = new UserManager(conn);

        scrollList = getScrollsForAll();

        if(scrollList.size() > 0) {
            //get total scroll number and use it for range of random
            day = Math.abs(seed - theDay) % scrollList.size();
            randomScroll = scrollList.get(day);
            return randomScroll;
        }
        return null;
    }



    public int getTotalNumberOfDownloads() throws SQLException {
        String sql = "SELECT SUM(downloads) AS totalDownloads\n" +
                "FROM Scrolls;";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.getInt("totalDownloads");
        }
    }

    public int getTotalNumberOfScrolls() throws SQLException {
        String sql = "SELECT COUNT(*) AS totalScrolls\n" +
                "FROM Scrolls;";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.getInt("totalScrolls");
        }
    }

    public Scroll getScrollByID(int scrollID) throws SQLException {
        this.scrollList.clear();
        String sql = "SELECT uploadDate, scrollName, filename, keyID, downloads\n" +
                "FROM Scrolls\n"+
                "WHERE scrollID = ?\n" +
                "LIMIT 1;";

        UserManager userManager = new UserManager(conn);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, scrollID);
            ResultSet rs = pstmt.executeQuery();

            // create a new table
            while (rs.next()) {
                String uploadDate = rs.getString("uploadDate");
                String scrollName = rs.getString("scrollName");
                String filename = rs.getString("filename");
                String keyID = rs.getString("keyID");
                Integer downloads = rs.getInt("downloads");
                return new Scroll(scrollID, uploadDate, scrollName, filename, userManager.findUserByID(keyID), downloads);
            }
        }
        return null;
    }

    public Scroll uploadScroll(String scrollName, String filename, User user) throws SQLException {
        return uploadScroll(scrollName, filename, user, 0);
    }

    /**
     * Upload scroll with custom number of downloads. For testing only.
     *
     * @param filename
     * @param user
     * @param downloads
     * @return
     */
    public Scroll uploadScroll(String scrollName, String filename, User user, int downloads) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy @ HH:mm:ss");
        String uploadDate = LocalDateTime.now().format(formatter);
        String sql = "INSERT INTO Scrolls (uploadDate, scrollName, filename, keyID, downloads) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, uploadDate);
            pstmt.setString(2, scrollName);
            pstmt.setString(3, filename);
            pstmt.setString(4, user.getKeyID());
            pstmt.setInt(5, downloads);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                String sql2 = "SELECT scrollID FROM Scrolls WHERE scrollName = ? ORDER BY scrollID ASC LIMIT 1";

                PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setString(1, scrollName);

                ResultSet rs = pstmt2.executeQuery();
                int scrollID = rs.getInt("scrollID");
                return new Scroll(scrollID, uploadDate, scrollName, filename, user, downloads);
            }
        } catch (SQLException e) {
            try {
                return getScrollByScrollName(scrollName);
            } catch (SQLException ex) {
                return null;
            }
        }
        return null;
    }

    /**
     * Delete the designated scroll from the database
     * @param scroll scroll to be deleted
     * @param user Current User to verify if the user is capable of deleting scroll
     * @return the deleted scroll
     */
    public Scroll deleteScroll(Scroll scroll, User user) throws SQLException {
        String keyID = scroll.getUploader().getKeyID();
        File vsasFolder = new File(System.getProperty("user.home"), ".vsas");
        if ((user.getAccountType().equals("Admin")) || (user.getKeyID().equals(keyID))) {
            File fileToDelete = new File(vsasFolder, String.format("%d_%s", scroll.getScrollID(), scroll.getFilename()));
            if (fileToDelete.delete()) {
                String sql = String.format("DELETE FROM Scrolls WHERE scrollID = %s", scroll.getScrollID());
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                return scroll;
            }
        }
        return null;
    }

    /**
     * Update data of a warehouse specified by the id
     *
     * @param scrollToBeDownloaded scroll downloaded successfully
     */
    public void increaseDownload(Scroll scrollToBeDownloaded) throws SQLException {
        Integer id = scrollToBeDownloaded.getScrollID();
        Integer count = scrollToBeDownloaded.getDownloads() + 1;
        String sql = "UPDATE Scrolls " +
                "SET downloads = ? " +
                "WHERE scrollID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, count);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    public ArrayList<Scroll> getScrollsForUser(User activeUser) throws SQLException {
        ArrayList<Scroll> userScrolls = new ArrayList<Scroll>();
        String sql = "SELECT uploadDate, scrollID, scrollName, filename, keyID, downloads\n" +
                "FROM Scrolls\n" +
                "WHERE keyID = ?;";

        UserManager userManager = new UserManager(conn);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, activeUser.getKeyID());
            ResultSet rs = pstmt.executeQuery();

            // Create a new table.
            while (rs.next()) {
                String uploadDate = rs.getString("uploadDate");
                int scrollID = rs.getInt("scrollID");
                String scrollName = rs.getString("scrollName");
                String filename = rs.getString("filename");
                String keyID = rs.getString("keyID");
                int downloads = rs.getInt("downloads");
                userScrolls.add(new Scroll(scrollID, uploadDate, scrollName, filename, userManager.findUserByID(keyID), downloads));
            }
        }

        return userScrolls;
    }

    public Scroll getScrollByScrollName(String scrollName) throws SQLException {
        this.scrollList.clear();
        String sql = "SELECT uploadDate, scrollID, scrollName, filename, keyID, downloads\n" +
                "FROM Scrolls\n" +
                "WHERE scrollName = ?\n" +
                "LIMIT 1;";

        UserManager userManager = new UserManager(conn);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, scrollName);
            ResultSet rs = pstmt.executeQuery();

            // create a new table
            while (rs.next()) {
                String uploadDate = rs.getString("uploadDate");
                int scrollID = rs.getInt("scrollID");
                String filename = rs.getString("filename");
                String keyID = rs.getString("keyID");
                Integer downloads = rs.getInt("downloads");
                return new Scroll(scrollID, uploadDate, scrollName, filename, userManager.findUserByID(keyID), downloads);
            }
        }

        return null;
    }
    public void updateScrollName(Scroll scroll, String newName) throws SQLException {
        String sql = "UPDATE Scrolls SET scrollName = ? WHERE scrollID = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        // Set the values of the parameters
        preparedStatement.setString(1, newName);
        preparedStatement.setInt(2, scroll.getScrollID());

        // Execute the update
        int rowsUpdated = preparedStatement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Scroll name updated successfully.");
        }

        preparedStatement.close();
    }

}
