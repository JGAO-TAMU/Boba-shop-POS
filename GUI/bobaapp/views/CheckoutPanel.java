package bobaapp.views;

import javax.swing.*;
import java.awt.*;
import bobaapp.models.CurrentOrder;
import bobaapp.models.OrderItem;
import bobaapp.database.OrdersDAO;
import bobaapp.database.InventoryDAO;
import bobaapp.database.DrinkIngredientsDAO;
import bobaapp.database.ModIngredientsDAO;

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
        
        try {
            // Temporarily disable the confirm button to prevent multiple orders
            confirmButton.setEnabled(false);
            
            // Reset sequences in a specific order
            // System.out.println("Resetting sequences before placing order...");
            // OrdersDAO.resetOrdersSequence();
            // OrdersDAO.resetDrinksSequence();
            
            System.out.println("Placing order...");
            // Place the order and get the order ID
            int orderId = OrdersDAO.placeOrder(1, currentOrder.getTotal());
            System.out.println("Order placed with ID: " + orderId);
            
            if (orderId != -1) {
                // Add each drink and its modifications
                boolean allItemsProcessed = true;
                
                for (OrderItem item : currentOrder.getItems()) {
                    // Add the drink and get its ID
                    int drinkId = OrdersDAO.addDrink(orderId, item.getMenuItem().getId());
                    
                    if (drinkId != -1) {
                        try {
                            // Update inventory for base drink ingredients
                            
                            // Add ice level modification if not default
                            // if (item.getIceQuantity() != 4) { // 4 is regular ice
                            //     int iceMod = getModIdForIceLevel(item.getIceQuantity());
                            //     if (iceMod != -1) {
                            //         OrdersDAO.addModification(drinkId, iceMod);
                            //         updateInventoryForModification(iceMod);
                            //     }
                            // } else {
                            //     // Even if using default ice, we need to update inventory for regular ice
                            //     int regularIceMod = getModIdForIceLevel(4);
                            //     updateInventoryForModification(regularIceMod);
                            // }
                            
                            // // Add sugar level modification if not default
                            // if (item.getSugarQuantity() != 4) { // 4 is 100% sugar
                            //     int sugarMod = getModIdForSugarLevel(item.getSugarQuantity());
                            //     if (sugarMod != -1) {
                            //         OrdersDAO.addModification(drinkId, sugarMod);
                            //         updateInventoryForModification(sugarMod);
                            //     }
                            // } else {
                            //     // Even if using default sugar, we need to update inventory for 100% sugar
                            //     int regularSugarMod = getModIdForSugarLevel(4);
                            //     updateInventoryForModification(regularSugarMod);
                            // }
                            
                            // Add topping modifications
                            for (String topping : item.getToppings()) {
                                int toppingId = getToppingModId(topping);
                                if (toppingId != -1) {
                                    OrdersDAO.addModification(drinkId, toppingId);
                                    updateInventoryForModification(toppingId);
                                }
                            }
                        } catch (Exception ex) {
                            System.err.println("Error updating inventory for drink " + 
                                item.getMenuItem().getName() + ": " + ex.getMessage());
                            ex.printStackTrace();
                            allItemsProcessed = false;
                        }
                    } else {
                        allItemsProcessed = false;
                        System.err.println("Failed to add drink to order: " + item.getMenuItem().getName());
                    }
                }
                
                // Clear the current order and update display
                currentOrder.clear();
                updateDisplay();
                
                // Update OrderHistoryPanel
                if (orderHistoryPanel != null) {
                    orderHistoryPanel.refreshOrderHistory();
                }
                
                // Update InventoryPanel with latest inventory data
                if (inventoryPanel != null) {
                    System.out.println("Refreshing inventory panel...");
                    inventoryPanel.refreshInventoryTable();
                } else {
                    System.err.println("ERROR: inventoryPanel is null, cannot refresh!");
                }
                
                if (allItemsProcessed) {
                    JOptionPane.showMessageDialog(this, "Order #" + orderId + " placed successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Order #" + orderId + " placed, but some items may not have been processed correctly. " +
                        "Check console for details.",
                        "Partial Success",
                        JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to place order. Please try again.", 
                    "Order Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
            confirmButton.setEnabled(true);
        } catch (Exception ex) {
            confirmButton.setEnabled(true);
            System.err.println("Error in confirmOrder: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error processing order: " + ex.getMessage(), 
                "Order Error", 
                JOptionPane.ERROR_MESSAGE);
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
