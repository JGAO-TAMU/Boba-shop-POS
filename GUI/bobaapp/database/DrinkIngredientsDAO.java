package bobaapp.database;

import java.sql.*;
import java.util.*;

public class DrinkIngredientsDAO {
    public static List<Map<String, Object>> getIngredientsForDrink(int menuId) {
        List<Map<String, Object>> ingredients = new ArrayList<>();
        String query = "SELECT di.ingredientid, i.name, di.quantityused FROM drinkingredients di " +
                      "JOIN inventory i ON di.ingredientid = i.ingredientid " +
                      "WHERE di.menuid = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, menuId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> ingredient = new HashMap<>();
                ingredient.put("ingredientId", rs.getInt("ingredientid"));
                ingredient.put("name", rs.getString("name"));
                ingredient.put("quantityUsed", rs.getInt("quantityused"));
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public static boolean addIngredientToDrink(int menuId, int ingredientId, int quantityUsed) {
        String query = "INSERT INTO drinkingredients (menuid, ingredientid, quantityused) VALUES (?, ?, ?);";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, menuId);
            stmt.setInt(2, ingredientId);
            stmt.setInt(3, quantityUsed);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    public static boolean isIngredientUsed(int ingredientId) {
        String query = "SELECT COUNT(*) FROM drinkingredients WHERE ingredientid = ?;";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, ingredientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
