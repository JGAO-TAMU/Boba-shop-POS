package bobaapp.database;

import bobaapp.models.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuDAO {
    // Get all menu items
    public static List<MenuItem> getMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT menuid, name, baseprice FROM menu ORDER BY name")) {
            
            while (rs.next()) {
                int id = rs.getInt("menuid");
                String name = rs.getString("name");
                double price = rs.getDouble("baseprice");
                
                menuItems.add(new MenuItem(id, name, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return menuItems;
    }
    
    // Get menu items by category (if category column exists)
    public static List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> menuItems = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT menuid, name, baseprice FROM menu WHERE category = ? ORDER BY name")) {
            
            pstmt.setString(1, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("menuid");
                    String name = rs.getString("name");
                    double price = rs.getDouble("baseprice");
                    
                    menuItems.add(new MenuItem(id, name, price));
                }
            }
        } catch (SQLException e) {
            // The category column might not exist, return all items instead
            return getMenuItems();
        }
        
        return menuItems;
    }
    
    // Get all menu categories (if category column exists)
    public static List<String> getMenuCategories() {
        List<String> categories = new ArrayList<>();
        Set<String> uniqueCategories = new HashSet<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT DISTINCT category FROM menu ORDER BY category")) {
            
            while (rs.next()) {
                String category = rs.getString("category");
                if (category != null && !category.isEmpty() && uniqueCategories.add(category)) {
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            // If category column doesn't exist, return default category
            categories.add("All Drinks");
        }
        
        // If no categories found, use a default
        if (categories.isEmpty()) {
            categories.add("All Drinks");
        }
        
        return categories;
    }
    
    // Add a new menu item
    public static boolean addMenuItem(String name, double price) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO menu (name, baseprice) VALUES (?, ?)")) {
            
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update a menu item's price
    public static boolean updatePrice(int menuId, double price) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE menu SET baseprice = ? WHERE menuid = ?")) {
            
            pstmt.setDouble(1, price);
            pstmt.setInt(2, menuId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete a menu item
    public static boolean deleteMenuItem(int menuId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM menu WHERE menuid = ?")) {
            
            pstmt.setInt(1, menuId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get menu item by ID
    public static MenuItem getMenuItemById(int menuId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT menuid, name, baseprice FROM menu WHERE menuid = ?")) {
            
            pstmt.setInt(1, menuId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("menuid");
                    String name = rs.getString("name");
                    double price = rs.getDouble("baseprice");
                    
                    return new MenuItem(id, name, price);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
