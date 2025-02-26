package bobaapp.utils;

import bobaapp.database.DatabaseConnection;

import java.sql.*;

/**
 * Emergency utility to forcibly repair the sequence and remove duplicates
 */
public class EmergencySequenceFix {
    
    public static void main(String[] args) {
        System.out.println("EMERGENCY SEQUENCE FIX UTILITY");
        System.out.println("------------------------------");
        
        fixOrdersTable();
        fixDrinksTable();
        fixModificationsTable();
        
        System.out.println("Emergency fix completed. Check the logs for details.");
    }
    
    private static void fixOrdersTable() {
        System.out.println("\nFixing orders table and sequence...");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start a transaction
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("BEGIN");
                
                // Lock the table
                stmt.execute("LOCK TABLE orders IN ACCESS EXCLUSIVE MODE");
                
                // Find the maximum order ID
                int maxOrderId = -1;
                try (ResultSet rs = stmt.executeQuery("SELECT MAX(orderid) FROM orders")) {
                    if (rs.next()) {
                        maxOrderId = rs.getInt(1);
                        System.out.println("Maximum order ID: " + maxOrderId);
                    }
                }
                
                // Reset the sequence to a safe value
                if (maxOrderId >= 0) {
                    int newSeqValue = maxOrderId + 10; // Add some buffer
                    stmt.execute("ALTER SEQUENCE orders_orderid_seq RESTART WITH " + newSeqValue);
                    System.out.println("Orders sequence set to " + newSeqValue);
                }
                
                // Commit the changes
                stmt.execute("COMMIT");
                System.out.println("Orders table fixed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error fixing orders table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void fixDrinksTable() {
        System.out.println("\nFixing drinks table and sequence...");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start a transaction
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("BEGIN");
                
                // Lock the table
                stmt.execute("LOCK TABLE drinks IN ACCESS EXCLUSIVE MODE");
                
                // Find the maximum drink ID
                int maxDrinkId = -1;
                try (ResultSet rs = stmt.executeQuery("SELECT MAX(drinkid) FROM drinks")) {
                    if (rs.next()) {
                        maxDrinkId = rs.getInt(1);
                        System.out.println("Maximum drink ID: " + maxDrinkId);
                    }
                }
                
                // Reset the sequence to a safe value
                if (maxDrinkId >= 0) {
                    int newSeqValue = maxDrinkId + 10; // Add some buffer
                    stmt.execute("ALTER SEQUENCE drinks_drinkid_seq RESTART WITH " + newSeqValue);
                    System.out.println("Drinks sequence set to " + newSeqValue);
                }
                
                // Commit the changes
                stmt.execute("COMMIT");
                System.out.println("Drinks table fixed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error fixing drinks table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void fixModificationsTable() {
        System.out.println("\nFixing modifications table and sequence...");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start a transaction
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("BEGIN");
                
                // Lock the table
                stmt.execute("LOCK TABLE modifications IN ACCESS EXCLUSIVE MODE");
                
                // Find the maximum modification ID
                int maxModId = -1;
                try (ResultSet rs = stmt.executeQuery("SELECT MAX(modid) FROM modifications")) {
                    if (rs.next()) {
                        maxModId = rs.getInt(1);
                        System.out.println("Maximum modification ID: " + maxModId);
                    }
                }
                
                // Reset the sequence to a safe value
                if (maxModId >= 0) {
                    int newSeqValue = maxModId + 10; // Add some buffer
                    stmt.execute("ALTER SEQUENCE modifications_modid_seq RESTART WITH " + newSeqValue);
                    System.out.println("Modifications sequence set to " + newSeqValue);
                }
                
                // Commit the changes
                stmt.execute("COMMIT");
                System.out.println("Modifications table fixed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error fixing modifications table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
