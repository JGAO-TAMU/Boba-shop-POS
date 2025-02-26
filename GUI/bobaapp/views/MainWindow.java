package bobaapp.views;

import bobaapp.utils.DatabaseUtils;

public class MainWindow {
    // Panel declarations
    private CheckoutPanel checkoutPanel;
    private OrderHistoryPanel orderHistoryPanel;
    private InventoryPanel inventoryPanel;
    static {
        // Initialize database sequences
        try {
            System.out.println("Initializing database sequences...");
            DatabaseUtils.performDatabaseMaintenance();
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public MainWindow() {
        // ...existing code...
        
        initializePanels();
        
        // ...existing code...
    }

    private void initializePanels() {
        // Initialize all panels
        checkoutPanel = new CheckoutPanel();
        orderHistoryPanel = new OrderHistoryPanel();
        inventoryPanel = new InventoryPanel();
        
        // Set up the cross-panel references for updates
        checkoutPanel.setOrderHistoryPanel(orderHistoryPanel);
        checkoutPanel.setInventoryPanel(inventoryPanel);
        
        System.out.println("Panel references initialized: CheckoutPanel -> OrderHistoryPanel and InventoryPanel");
        
        // Add panels to the tabbed pane or layout
        // ...existing panel addition code...
    }
}
