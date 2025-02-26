package bobaapp.database;

import bobaapp.models.InventoryItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryDAO {
    // Get all inventory items
    public static List<InventoryItem> getInventory() {
        List<InventoryItem> inventory = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ingredientid, name, quantity FROM inventory ORDER BY name")) {
            
            while (rs.next()) {
                int id = rs.getInt("ingredientid");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                inventory.add(new InventoryItem(id, name, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return inventory;
    }

    /**
     * Get all inventory items with their current quantities as maps
     * @return List of inventory items as maps with ingredientId, name, and quantity
     */
    public static List<Map<String, Object>> getInventoryAsMaps() {
        List<Map<String, Object>> inventory = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ingredientid, name, quantity FROM inventory ORDER BY name")) {
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("ingredientId", rs.getInt("ingredientid"));
                item.put("name", rs.getString("name"));
                item.put("quantity", rs.getInt("quantity"));
                
                inventory.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting inventory: " + e.getMessage());
            e.printStackTrace();
        }
        
        return inventory;
    }
    
    // Update an inventory item
    public static boolean updateInventory(int ingredientId, int newQuantity) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE inventory SET quantity = ? WHERE ingredientid = ?")) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, ingredientId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update quantity by adding/subtracting delta (negative for subtraction)
    public static boolean updateInventoryQuantity(int ingredientId, int delta) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE inventory SET quantity = quantity + ? WHERE ingredientid = ?")) {
            
            pstmt.setInt(1, delta);
            pstmt.setInt(2, ingredientId);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Updated ingredient " + ingredientId + " quantity by " + delta + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating inventory quantity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Add a new inventory item
    public static boolean addInventoryItem(String name, int quantity) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO inventory (name, quantity) VALUES (?, ?)")) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, quantity);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete an inventory item
    public static boolean deleteInventoryItem(int ingredientId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM inventory WHERE ingredientid = ?")) {
            
            pstmt.setInt(1, ingredientId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
