package bobaapp.database;

import bobaapp.models.InventoryItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
    public static List<InventoryItem> getInventory() {
        List<InventoryItem> inventory = new ArrayList<>();
        String query = "SELECT * FROM inventory;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                inventory.add(new InventoryItem(
                    rs.getInt("ingredientID"),
                    rs.getString("name"),
                    rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventory;
    }

    public static void updateInventory(int ingredientID, int newQuantity) {
        String query = "UPDATE inventory SET quantity = ? WHERE ingredientID = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, ingredientID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addInventoryItem(String name, int quantity) {
        String query = "INSERT INTO inventory (name, quantity) VALUES (?, ?);";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, name);
            stmt.setInt(2, quantity);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteInventoryItem(int ingredientID) {
        String query = "DELETE FROM inventory WHERE ingredientID = ?;";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, ingredientID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
