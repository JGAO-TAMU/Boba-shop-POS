package bobaapp.utils;

import bobaapp.database.DatabaseConnection;
import java.sql.*;

/**
 * Utility class for database maintenance operations.
 */
public class DatabaseUtils {
    
    /**
     * Fixes the drinks sequence to match the max drinkid in the table.
     * Can be called from any part of the application or standalone.
     */
    public static void fixDrinksSequence() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Find the maximum drink ID
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(drinkid), 0) + 1 FROM drinks")) {
                
                if (rs.next()) {
                    int nextId = rs.getInt(1);
                    
                    // Set the sequence to the next available ID using direct SQL (no parameters)
                    try (Statement updateStmt = conn.createStatement()) {
                        updateStmt.execute("ALTER SEQUENCE drinks_drinkid_seq RESTART WITH " + nextId);
                        System.out.println("Drinks sequence successfully reset to " + nextId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fixing drinks sequence: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fixes the modifications sequence to match the max modid in the table.
     */
    public static void fixModificationsSequence() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Find the maximum modification ID
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(modid), 0) + 1 FROM modifications")) {
                
                if (rs.next()) {
                    int nextId = rs.getInt(1);
                    
                    // Set the sequence to the next available ID
                    try (Statement updateStmt = conn.createStatement()) {
                        updateStmt.execute("ALTER SEQUENCE modifications_modid_seq RESTART WITH " + nextId);
                        System.out.println("Modifications sequence successfully reset to " + nextId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fixing modifications sequence: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Checks for and fixes any database inconsistencies.
     * This could be expanded to handle more types of issues.
     */
    public static void performDatabaseMaintenance() {
        fixDrinksSequence();
        fixModificationsSequence();
        // Add other maintenance tasks as needed
    }
    
    /**
     * Main method to run the utility as a standalone tool.
     */
    public static void main(String[] args) {
        System.out.println("Starting database maintenance...");
        performDatabaseMaintenance();
        System.out.println("Database maintenance completed.");
    }
}
