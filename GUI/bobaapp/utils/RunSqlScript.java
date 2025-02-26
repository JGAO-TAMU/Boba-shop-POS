package bobaapp.utils;

import bobaapp.database.DatabaseConnection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Utility class to run SQL scripts for database maintenance.
 */
public class RunSqlScript {
    
    /**
     * Runs an SQL script file
     * @param scriptPath Path to the SQL script file
     * @return true if successful, false otherwise
     */
    public static boolean runScript(String scriptPath) {
        try (Connection conn = DatabaseConnection.getConnection();
             BufferedReader reader = new BufferedReader(new FileReader(scriptPath))) {
            
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                sqlBuilder.append(line);
                
                // If the line ends with a semicolon, execute the SQL statement
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString();
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql);
                        System.out.println("Executed: " + sql);
                    }
                    sqlBuilder = new StringBuilder();
                } else {
                    // Add a space for multi-line statements
                    sqlBuilder.append(" ");
                }
            }
            
            System.out.println("SQL script executed successfully!");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error executing SQL script: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Main method to run the fix_sequences.sql script
     */
    public static void main(String[] args) {
        String scriptPath = "/Users/zakarymobarak/Documents/FINALPROJEcT/project2-team10/GUI/bobaapp/utils/fix_sequences.sql";
        boolean success = runScript(scriptPath);
        System.out.println(success ? "Database sequences fixed successfully!" : "Failed to fix database sequences.");
    }
}
