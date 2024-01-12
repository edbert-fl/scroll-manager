package soft2412.a2.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private Connection conn;

    /**
     * Setup our userDatabase
     * @param dataBaseName
     */
    public Database(String dataBaseName) throws SQLException {
        String databaseURL = "jdbc:sqlite:src/main/database/" + dataBaseName;
        createNewDatabase(databaseURL);
        this.conn = connect(databaseURL);
        this.createTables();
    }

    /**
     * Creates a new SQLite database.
     * @param url The name of the new database to be created.
     */
    public void createNewDatabase(String url) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
            }
        }
    }

    /**
     * Establishes a connection to an SQLite database.
     * @return A connection to the SQLite database.
     */
    public static Connection connect(String url) throws SQLException {
        Connection conn = null;
        conn = DriverManager.getConnection(url);
        return conn;
    }

    /**
     * Creates the 'Users' table in an SQLite database if it does not already exist.
     */
    public void createTables(){
        if (this.conn != null){
            String sql = "CREATE TABLE IF NOT EXISTS Users (\n"
                    + "keyID VARCHAR(1000) PRIMARY KEY,\n"
                    + "userName VARCHAR(100),\n"
                    + "accountType VARCHAR(10),\n"
                    + "email VARCHAR(100),\n"
                    + "phoneNumber VARCHAR(100),\n"
                    + "salt VARCHAR(1000),\n"
                    + "hashedPassword VARCHAR(1000),\n"
                    + "firstName VARCHAR(100),\n"
                    + "lastName VARCHAR(100));";
            String sql2 = "CREATE TABLE IF NOT EXISTS Scrolls (\n"
                    + "scrollID INTEGER PRIMARY KEY,\n"
                    + "uploadDate VARCHAR(1000),\n"
                    + "scrollName VARCHAR(1000) UNIQUE,\n"
                    + "filename VARCHAR(10000),\n"
                    + "keyID VARCHAR(1000) REFERENCES Users(keyID)\n"
                    + "ON DELETE CASCADE,\n"
                    + "downloads INTEGER\n"
                    + ");";


            try (Statement stmt = conn.createStatement()) {
                // create a new table
                stmt.executeUpdate(sql);
                stmt.executeUpdate(sql2);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public Connection getConn(){
        return this.conn;
    }

}
