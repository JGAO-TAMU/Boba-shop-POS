package bobaapp.utils;

import bobaapp.database.InventoryDAO;
import bobaapp.models.IngredientUsage;

import java.util.Map;
import java.util.List;

/**
 * Utility class to test inventory updates directly without the UI.
 * Can be run as a standalone application.
 */
public class InventoryUpdateTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Inventory Updates ===");
        
        // 1. Get current inventory
        List<Map<String, Object>> initialInventory = InventoryDAO.getInventoryAsMaps();
        
        System.out.println("Initial Inventory:");
        for (Map<String, Object> item : initialInventory) {
            System.out.println(item.get("ingredientId") + ": " + item.get("name") + " - " + item.get("quantity"));
        }
        
        // 2. Update an item (ingredient ID 1, decrease by 5)
        int ingredientIdToTest = 1;
        int quantityChange = -5;
        
        System.out.println("\nUpdating ingredient ID " + ingredientIdToTest + " by " + quantityChange + "...");
        boolean updateSuccessful = InventoryDAO.updateInventoryQuantity(ingredientIdToTest, quantityChange);
        
        System.out.println("Update successful: " + updateSuccessful);
        
        // 3. Check updated inventory
        List<Map<String, Object>> updatedInventory = InventoryDAO.getInventoryAsMaps();
        
        System.out.println("\nUpdated Inventory:");
        for (Map<String, Object> item : updatedInventory) {
            if ((int)item.get("ingredientId") == ingredientIdToTest) {
                System.out.println(item.get("ingredientId") + ": " + item.get("name") + " - " + item.get("quantity") + " <-- UPDATED");
            } else {
                System.out.println(item.get("ingredientId") + ": " + item.get("name") + " - " + item.get("quantity"));
            }
        }
        
        // 4. Reset the test (add back the quantity)
        System.out.println("\nResetting test (adding back " + (-quantityChange) + " to ingredient ID " + ingredientIdToTest + ")...");
        boolean resetSuccessful = InventoryDAO.updateInventoryQuantity(ingredientIdToTest, -quantityChange);
        
        System.out.println("Reset successful: " + resetSuccessful);
        
        System.out.println("\nTest complete.");
    }
}
