package bobaapp.utils;

import bobaapp.models.Modification;
import bobaapp.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class DbUtil {
    
    // Fetch all modifications from the database
    public static List<Modification> getModifications() {
        List<Modification> modifications = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT modmenuid, name, price FROM modificationsmenu ORDER BY name")) {
            
            while (rs.next()) {
                int id = rs.getInt("modmenuid");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                
                // Simple categorization based on name
                String category = "Toppings"; // Default category
                
                if (name.toLowerCase().contains("ice")) {
                    category = "Ice";
                } else if (name.toLowerCase().contains("sugar")) {
                    category = "Sugar";
                }
                
                modifications.add(new Modification(id, name, price, category));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return modifications;
    }
    
    // Group modifications by category
    public static Map<String, List<Modification>> getModificationsByCategory() {
        List<Modification> allModifications = getModifications();
        Map<String, List<Modification>> categorizedMods = new HashMap<>();
        
        // Initialize category lists
        categorizedMods.put("Ice", new ArrayList<>());
        categorizedMods.put("Sugar", new ArrayList<>());
        categorizedMods.put("Toppings", new ArrayList<>());
        
        // Sort modifications into categories
        for (Modification mod : allModifications) {
            categorizedMods.get(mod.getCategory()).add(mod);
        }
        
        // Sort the sugar options in the desired order
        List<Modification> sugarOptions = categorizedMods.get("Sugar");
        sugarOptions.sort(Comparator.comparingInt(mod -> {
            switch (mod.getName()) {
                case "Sugar": return 0;
                case "75% Sugar": return 1;
                case "50% Sugar": return 2;
                case "25% Sugar": return 3;
                case "0% Sugar": return 4;
                default: return 5;
            }
        }));
        
        // Sort the ice options in the desired order
        List<Modification> iceOptions = categorizedMods.get("Ice");
        iceOptions.sort(Comparator.comparingInt(mod -> {
            switch (mod.getName()) {
                case "Ice": return 0;
                case "Less Ice": return 1;
                case "No Ice": return 2;
                default: return 3;
            }
        }));
        
        return categorizedMods;
    }
}
