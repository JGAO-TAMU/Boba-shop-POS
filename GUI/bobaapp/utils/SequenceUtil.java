package bobaapp.utils;

import bobaapp.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SequenceUtil {
    
    // Reset sequences to match the current max values in tables
    public static void resetSequences() {
        resetSequence("orders", "orderid");
        resetSequence("drinks", "drinkid");
        resetSequence("modifications", "modid");
        resetSequence("menu", "menuid");
        resetSequence("modificationsmenu", "modmenuid");
        resetSequence("inventory", "ingredientid");
        resetSequence("employee", "employeeid");
    }
    
    private static void resetSequence(String tableName, String columnName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // First, get the sequence name
            String sequenceName = null;
            String sql = "SELECT pg_get_serial_sequence(?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tableName);
                pstmt.setString(2, columnName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        sequenceName = rs.getString(1);
                    }
                }
            }
            
            if (sequenceName != null) {
                // Get max ID value
                int maxId = 0;
                sql = "SELECT COALESCE(MAX(" + columnName + "), 0) FROM " + tableName;
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            maxId = rs.getInt(1);
                        }
                    }
                }
                
                // Reset sequence
                sql = "SELECT pg_catalog.setval(?, ?, true)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, sequenceName);
                    pstmt.setInt(2, maxId);
                    pstmt.executeQuery();
                }
                
                System.out.println("Reset sequence for " + tableName + "." + columnName + " to " + maxId);
            } else {
                System.out.println("No sequence found for " + tableName + "." + columnName);
            }
        } catch (SQLException e) {
            System.err.println("Error resetting sequence for " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
