package bobaapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DATABASE_NAME = "team_10_db";
    private static final String DATABASE_USER = "team_10";
    private static final String DATABASE_PASSWORD = "nexoloftasmr";
    private static final String DATABASE_URL = 
        "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + DATABASE_NAME;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }
}
