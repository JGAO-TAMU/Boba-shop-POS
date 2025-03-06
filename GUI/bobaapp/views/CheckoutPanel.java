package bobaapp.views;

import javax.naming.directory.ModificationItem;
import javax.swing.*;
import java.awt.*;
import bobaapp.models.CurrentOrder;
import bobaapp.models.OrderItem;
import bobaapp.database.OrdersDAO;
import bobaapp.database.InventoryDAO;
import bobaapp.database.DrinkIngredientsDAO;
import bobaapp.database.ModIngredientsDAO;
import java.util.ArrayList;
import java.util.List;

public class CheckoutPanel extends JPanel {
    private JTextArea checkoutTextArea;
    private JButton confirmButton;
    private OrderHistoryPanel orderHistoryPanel;
    private InventoryPanel inventoryPanel;
    
    public CheckoutPanel() {
        setLayout(new BorderLayout());
        
        checkoutTextArea = new JTextArea();
        checkoutTextArea.setEditable(false);
        checkoutTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        confirmButton = new JButton("Confirm Order");
        confirmButton.setBackground(new Color(144, 238, 144));
        confirmButton.addActionListener(e -> confirmOrder());
        
        add(new JScrollPane(checkoutTextArea), BorderLayout.CENTER);
        add(confirmButton, BorderLayout.SOUTH);
        
        // Update the display when shown
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                updateDisplay();
            }
        });
    }
    
    // Set reference to OrderHistoryPanel for updates
    public void setOrderHistoryPanel(OrderHistoryPanel panel) {
        this.orderHistoryPanel = panel;
    }
    
    // Set reference to InventoryPanel for updates
    public void setInventoryPanel(InventoryPanel panel) {
        this.inventoryPanel = panel;
    }
    
    private void updateDisplay() {
        CurrentOrder currentOrder = CurrentOrder.getInstance();
        StringBuilder sb = new StringBuilder();
        
        for (OrderItem item : currentOrder.getItems()) {
            sb.append(item.getDescription()).append("\n\n");
        }
        
        sb.append("=========================\n");
        sb.append("Total: $").append(String.format("%.2f", currentOrder.getTotal()));
        
        checkoutTextArea.setText(sb.toString());
    }
    
    private void confirmOrder() {
        CurrentOrder currentOrder = CurrentOrder.getInstance();
        
        // Immediately disable button to prevent double submission
        confirmButton.setEnabled(false);
        
        try {
            System.out.println("Placing order...");
            int orderId = OrdersDAO.placeOrder(1, currentOrder.getTotal());
            
            // Only process if we got a valid order ID
            if (orderId != -1) {
                // Make a copy of the items to prevent concurrent modification
                List<OrderItem> itemsCopy = new ArrayList<>(currentOrder.getItems());
                
                // Process order items...
                for (OrderItem item : itemsCopy) {
                    // Get the menu item ID and update inventory
                    int menuId = item.getMenuItem().getId();
                    int drinkId = OrdersDAO.addDrink(orderId, menuId);
                    
                    // Update inventory for the base drink
                    updateInventoryForDrink(menuId);
                    
                    // Process modifications for this drink (ice, sugar, toppings, etc.)
                    // Process toppings
                    if (item.getToppings() != null && !item.getToppings().isEmpty()) {
                        for (String topping : item.getToppings()) {
                            int toppingModId = getToppingModId(topping);
                            if (toppingModId > 0) {
                                try {
                                    OrdersDAO.addModification(drinkId, toppingModId);
                                    updateInventoryForModification(toppingModId);
                                    System.out.println("Added topping modification: " + topping + " (ID: " + toppingModId + ")");
                                } catch (Exception e) {
                                    System.err.println("Error adding topping modification: " + e.getMessage());
                                    // Continue processing other toppings
                                }
                            }
                        }
                    }
                }
                    
                // Only show message and clear order AFTER processing all items
                JOptionPane.showMessageDialog(this,
                    "Order placed successfully!",
                    "Order Confirmation", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Clear the order AFTER processing all items
                currentOrder.clear();
                System.out.println("Cleared current order");
                updateDisplay();
                
                // Update panels if needed
                if (orderHistoryPanel != null) {
                    orderHistoryPanel.refreshOrderHistory();
                }
                if (inventoryPanel != null) {
                    inventoryPanel.refreshInventoryTable();
                }
                confirmButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to place order. Please try again.", 
                    "Order Error", 
                    JOptionPane.ERROR_MESSAGE);
                // Re-enable button on failure
                confirmButton.setEnabled(true);
            }
        } catch (Exception ex) {
            System.err.println("Error in confirmOrder: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error processing order: " + ex.getMessage(), 
                "Order Error", 
                JOptionPane.ERROR_MESSAGE);
            // Re-enable button on exception
            confirmButton.setEnabled(true);
        }
    }
    
    // Update inventory for drink base ingredients
    private void updateInventoryForDrink(int menuId) {
        // Get all ingredients needed for this drink from DrinkIngredients table
        DrinkIngredientsDAO.getDrinkIngredients(menuId).forEach(ingredient -> {
            int ingredientId = ingredient.getIngredientId();
            int quantityUsed = ingredient.getQuantityUsed();
            
            // Subtract used quantity from inventory
            boolean updated = InventoryDAO.updateInventoryQuantity(ingredientId, -quantityUsed);
            System.out.println("Updated ingredient " + ingredientId + " by -" + quantityUsed + ": " + updated);
        });
    }
    
    // Update inventory for modifications
    private void updateInventoryForModification(int modMenuId) {
        // Get all ingredients needed for this modification from ModIngredients table
        ModIngredientsDAO.getModIngredients(modMenuId).forEach(ingredient -> {
            int ingredientId = ingredient.getIngredientId();
            int quantityUsed = ingredient.getQuantityUsed();
            
            // Subtract used quantity from inventory
            boolean updated = InventoryDAO.updateInventoryQuantity(ingredientId, -quantityUsed);
            System.out.println("Updated ingredient " + ingredientId + " by -" + quantityUsed + ": " + updated);
        });
    }
    
    // Map ice level quantities to modification IDs
    private int getModIdForIceLevel(int iceQuantity) {
        switch (iceQuantity) {
            case 0: return getModIdByName("No Ice");
            case 2: return getModIdByName("Less Ice");
            case 4: return getModIdByName("Ice");
            case 5: return getModIdByName("Extra Ice");
            default: return -1;
        }
    }
    
    // Map sugar level quantities to modification IDs
    private int getModIdForSugarLevel(int sugarQuantity) {
        switch (sugarQuantity) {
            case 0: return getModIdByName("0% Sugar");
            case 1: return getModIdByName("25% Sugar");
            case 2: return getModIdByName("50% Sugar");
            case 3: return getModIdByName("75% Sugar");
            case 4: return getModIdByName("Sugar");
            default: return -1;
        }
    }
    
    // Helper method to get modification ID by name
    private int getModIdByName(String name) {
        return OrdersDAO.getModMenuIdByName(name);
    }

    // Helper method to map topping names to modification menu IDs
    private int getToppingModId(String topping) {
        return getModIdByName(topping);
    }
}
