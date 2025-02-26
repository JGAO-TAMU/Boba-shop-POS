package bobaapp.views;

import javax.swing.*;
import java.awt.*;
import bobaapp.models.MenuItem;

public class MainPanel extends JPanel {
    private CardLayout cardLayout;
    
    // Main panels
    private MenuPanel menuPanel;
    private ModificationsPanel modificationsPanel;
    private CheckoutPanel checkoutPanel;
    private OrderHistoryPanel orderHistoryPanel;
    private InventoryPanel inventoryPanel;
    
    public MainPanel() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        
        // Initialize panels
        menuPanel = new MenuPanel();
        modificationsPanel = new ModificationsPanel();
        orderHistoryPanel = new OrderHistoryPanel();
        inventoryPanel = new InventoryPanel();
        checkoutPanel = new CheckoutPanel();
        
        // Set up cross-panel references
        checkoutPanel.setOrderHistoryPanel(orderHistoryPanel);
        checkoutPanel.setInventoryPanel(inventoryPanel);
        
        // Add all panels to the card layout
        add(menuPanel, "Menu");
        add(modificationsPanel, "Modifications");
        add(checkoutPanel, "Checkout");
        add(orderHistoryPanel, "OrderHistory");
        add(inventoryPanel, "Inventory");
        
        // Set up navigation logic between panels
        setupPanelNavigation();
        
        // Initially show menu panel
        cardLayout.show(this, "Menu");
    }
    
    private void setupPanelNavigation() {
        // Menu to modifications navigation
        menuPanel.addMenuItemSelectionListener(item -> {
            modificationsPanel.setDrink(item);
            cardLayout.show(MainPanel.this, "Modifications");
        });
    }
    
    // Methods to show specific panels
    public void showMenuPanel() {
        cardLayout.show(this, "Menu");
    }
    
    public void showModificationsPanel() {
        cardLayout.show(this, "Modifications");
    }
    
    public void showCheckoutPanel() {
        cardLayout.show(this, "Checkout");
    }
    
    public void showOrderHistoryPanel() {
        orderHistoryPanel.refreshOrderHistory();
        cardLayout.show(this, "OrderHistory");
    }
    
    public void showInventoryPanel() {
        inventoryPanel.refreshInventoryTable();
        cardLayout.show(this, "Inventory");
    }
}
