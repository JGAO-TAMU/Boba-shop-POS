package GUI.bobaapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DATABASE_NAME = "";
    private static final String DATABASE_USER = "";
    private static final String DATABASE_PASSWORD = "";
    private static final String DATABASE_URL = 
        "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + DATABASE_NAME;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }
}
