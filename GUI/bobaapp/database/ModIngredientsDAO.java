package bobaapp.database;

import bobaapp.models.IngredientUsage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModIngredientsDAO {
    // Get ingredients used for a modification
    public static List<IngredientUsage> getModIngredients(int modMenuId) {
        List<IngredientUsage> ingredients = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT ingredientid, quantityused FROM modingredients WHERE modmenuid = ?")) {
            
            pstmt.setInt(1, modMenuId);
            
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
}
