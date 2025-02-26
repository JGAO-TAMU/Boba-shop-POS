package bobaapp.views;

import bobaapp.database.InventoryDAO;
import bobaapp.database.DrinkIngredientsDAO;
import bobaapp.models.InventoryItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class InventoryPanel extends JPanel {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JLabel summaryLabel;
    private static final int LOW_STOCK_THRESHOLD = 10;
    private boolean showLowStockWarnings = true;

    public InventoryPanel() {
        setLayout(new BorderLayout());
        
        // Create table model with non-editable cells
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable by default
            }
        };
        
        // Add columns to table model
        tableModel.addColumn("ID");
        tableModel.addColumn("Ingredient Name");
        tableModel.addColumn("Quantity");
        
        // Create table and set properties
        inventoryTable = new JTable(tableModel);
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        
        // Create summary label
        summaryLabel = new JLabel("Loading inventory...");
        
        // Add to panel
        add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
        add(summaryLabel, BorderLayout.SOUTH);
        
        // Load data when panel is shown
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshInventoryTable();
            }
        });
        
        // Create update button
        JButton updateButton = new JButton("Update Quantity");
        updateButton.addActionListener(e -> showUpdateDialog());
        
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add New Item");
        JButton deleteButton = new JButton("Delete Item");
        
        addButton.addActionListener(e -> showAddItemDialog());
        updateButton.addActionListener(e -> showUpdateDialog());
        deleteButton.addActionListener(e -> showDeleteDialog());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        refreshInventoryTable();
    }

    private void showUpdateDialog() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to update.");
            return;
        }

        int itemId = (int) inventoryTable.getValueAt(selectedRow, 0);
        String itemName = (String) inventoryTable.getValueAt(selectedRow, 1);
        
        String input = JOptionPane.showInputDialog(this, 
            "Enter new quantity for " + itemName + ":",
            "Update Quantity",
            JOptionPane.PLAIN_MESSAGE);
            
        if (input != null && !input.trim().isEmpty()) {
            try {
                int newQuantity = Integer.parseInt(input);
                InventoryDAO.updateInventory(itemId, newQuantity);
                refreshInventoryTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        }
    }

    private void showAddItemDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        
        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Add New Inventory Item", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an item name.");
                    return;
                }

                if (InventoryDAO.addInventoryItem(name, quantity)) {
                    refreshInventoryTable();
                    JOptionPane.showMessageDialog(this, "Item added successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add item.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        }
    }

    private void showDeleteDialog() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
            return;
        }

        int itemId = (int) inventoryTable.getValueAt(selectedRow, 0);
        String itemName = (String) inventoryTable.getValueAt(selectedRow, 1);
        
        // Check if ingredient is used in any drink recipe
        if (DrinkIngredientsDAO.isIngredientUsed(itemId)) {
            JOptionPane.showMessageDialog(this,
                "Cannot delete " + itemName + " because it is used in one or more drink recipes.",
                "Delete Failed",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete " + itemName + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (InventoryDAO.deleteInventoryItem(itemId)) {
                refreshInventoryTable();
                JOptionPane.showMessageDialog(this, "Item deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete item.");
            }
        }
    }

    // Public method to refresh inventory table
    public void refreshInventoryTable() {
        // Clear the existing data
        tableModel.setRowCount(0);
        
        // Load fresh inventory data
        List<Map<String, Object>> inventoryItems = InventoryDAO.getInventoryAsMaps();
        
        // Populate the table with the new data
        for (Map<String, Object> item : inventoryItems) {
            tableModel.addRow(new Object[] {
                item.get("ingredientId"),
                item.get("name"),
                item.get("quantity")
            });
        }
        
        // Update any visualization or summary information
        updateInventorySummary();
        
        // Notify the user if any items are low in stock
        checkLowStockItems();
        
        // Debug message to confirm refresh
        System.out.println("Inventory table refreshed with " + inventoryItems.size() + " items");
    }
    
    /**
     * Update any summary information about inventory
     */
    private void updateInventorySummary() {
        // Implementation depends on your UI design
        // For example, you might update a status label showing total items in inventory
        if (summaryLabel != null) {
            int itemCount = inventoryTable.getRowCount();
            int lowStockCount = countLowStockItems();
            summaryLabel.setText(String.format("Total Items: %d | Low Stock: %d", itemCount, lowStockCount));
        }
    }
    
    /**
     * Count items that are low in stock
     */
    private int countLowStockItems() {
        int count = 0;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int quantity = ((Number) tableModel.getValueAt(i, 2)).intValue();
            if (quantity < LOW_STOCK_THRESHOLD) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Check for items that are low in stock and highlight them or show warnings
     */
    private void checkLowStockItems() {
        boolean hasLowStock = false;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int quantity = ((Number) tableModel.getValueAt(i, 2)).intValue();
            if (quantity < LOW_STOCK_THRESHOLD) {
                hasLowStock = true;
                // You might highlight these rows or mark them in some way
            }
        }
        
        if (hasLowStock && showLowStockWarnings) {
            // Show a warning about low stock items
            JOptionPane.showMessageDialog(
                this,
                "Some inventory items are running low on stock!",
                "Low Stock Warning",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
}
