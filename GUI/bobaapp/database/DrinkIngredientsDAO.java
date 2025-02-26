package bobaapp.database;

import bobaapp.models.IngredientUsage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DrinkIngredientsDAO {
    // Get ingredients used for a drink
    public static List<IngredientUsage> getDrinkIngredients(int menuId) {
        List<IngredientUsage> ingredients = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT ingredientid, quantityused FROM drinkingredients WHERE menuid = ?")) {
            
            pstmt.setInt(1, menuId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int ingredientId = rs.getInt("ingredientid");
                    int quantityUsed = rs.getInt("quantityused");
                    ingredients.add(new IngredientUsage(ingredientId, quantityUsed));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return ingredients;
    }
    
    // Get ingredients for a drink with detailed information
    public static List<Map<String, Object>> getIngredientsForDrink(int menuId) {
        List<Map<String, Object>> ingredients = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT di.ingredientid, di.quantityused, i.name " +
                "FROM drinkingredients di " +
                "JOIN inventory i ON di.ingredientid = i.ingredientid " +
                "WHERE di.menuid = ?")) {
            
            pstmt.setInt(1, menuId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> ingredient = new HashMap<>();
                    ingredient.put("ingredientId", rs.getInt("ingredientid"));
                    ingredient.put("quantityUsed", rs.getInt("quantityused"));
                    ingredient.put("name", rs.getString("name"));
                    
                    ingredients.add(ingredient);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return ingredients;
    }
    
    // Check if an ingredient is used in any drink
    public static boolean isIngredientUsed(int ingredientId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM drinkingredients WHERE ingredientid = ?")) {
            
            pstmt.setInt(1, ingredientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    public static boolean addIngredientToDrink(int menuId, int ingredientId, int quantityUsed) {
        // First, check if this ingredient is already used in this drink
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT 1 FROM drinkingredients WHERE menuid = ? AND ingredientid = ?")) {
            
            checkStmt.setInt(1, menuId);
            checkStmt.setInt(2, ingredientId);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Update existing ingredient quantity
                    try (PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE drinkingredients SET quantityused = ? WHERE menuid = ? AND ingredientid = ?")) {
                        
                        updateStmt.setInt(1, quantityUsed);
                        updateStmt.setInt(2, menuId);
                        updateStmt.setInt(3, ingredientId);
                        
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    // Insert new ingredient
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO drinkingredients (menuid, ingredientid, quantityused) VALUES (?, ?, ?)")) {
                        
                        insertStmt.setInt(1, menuId);
                        insertStmt.setInt(2, ingredientId);
                        insertStmt.setInt(3, quantityUsed);
                        
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    public static boolean clearIngredientsForDrink(int menuId) {
        String query = "DELETE FROM drinkingredients WHERE menuid = ?;";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, menuId);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Remove a specific ingredient from a drink
    public static boolean removeIngredientFromDrink(int menuId, int ingredientId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM drinkingredients WHERE menuid = ? AND ingredientid = ?")) {
            
            pstmt.setInt(1, menuId);
            pstmt.setInt(2, ingredientId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
