package bobaapp.views;

import javax.swing.*;
import java.awt.*;
import bobaapp.models.CurrentOrder;
import bobaapp.models.OrderItem;
import bobaapp.database.OrdersDAO;

public class CheckoutPanel extends JPanel {
    private JTextArea checkoutTextArea;
    private JButton confirmButton;
    
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
        // Place the order and get the order ID
        int orderId = OrdersDAO.placeOrder(1, currentOrder.getTotal());
        
        if (orderId != -1) {
            // Add each drink and its modifications
            for (OrderItem item : currentOrder.getItems()) {
                // Add the drink and get its ID
                int drinkId = OrdersDAO.addDrink(orderId, item.getMenuItem().getId());
                
                if (drinkId != -1) {
                    // Add ice level modification if not default
                    if (item.getIceQuantity() != 4) { // 4 is regular ice
                        OrdersDAO.addModification(drinkId, item.getIceQuantity());
                    }
                    
                    // Add sugar level modification if not default
                    if (item.getSugarQuantity() != 4) { // 4 is 100% sugar
                        OrdersDAO.addModification(drinkId, item.getSugarQuantity() + 5); // Offset for sugar mods
                    }
                    
                    // Add topping modifications
                    for (String topping : item.getToppings()) {
                        int toppingId = getToppingModId(topping);
                        if (toppingId != -1) {
                            OrdersDAO.addModification(drinkId, toppingId);
                        }
                    }
                }
            }
            
            currentOrder.clear();
            updateDisplay();
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to place order.");
        }
    }

    // Helper method to map topping names to modification menu IDs
    private int getToppingModId(String topping) {
        // These IDs should match your modificationsmenu table
        switch(topping) {
            case "Boba": return 10;
            case "Mini Pearls": return 11;
            case "Strawberry Boba": return 12;
            case "Aloe Vera": return 13;
            case "Coffee Jelly": return 14;
            case "Coconut Jelly": return 15;
            case "Egg Pudding": return 16;
            case "Mango Jelly": return 17;
            case "Grass Jelly": return 18;
            case "Almond Pudding": return 19;
            case "Lychee": return 20;
            case "White Pearls": return 21;
            default: return -1;
        }
    }
}
