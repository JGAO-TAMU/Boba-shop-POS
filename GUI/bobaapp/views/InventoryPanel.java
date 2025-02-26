package bobaapp.views;

import bobaapp.database.InventoryDAO;
import bobaapp.database.DrinkIngredientsDAO;
import bobaapp.models.InventoryItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;

    public InventoryPanel() {
        setLayout(new BorderLayout());
        
        // Create table model
        String[] columns = {"ID", "Item Name", "Quantity"};
        tableModel = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableModel);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        add(scrollPane, BorderLayout.CENTER);
        
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

    private void refreshInventoryTable() {
        tableModel.setRowCount(0);
        List<InventoryItem> inventory = InventoryDAO.getInventory();
        for (InventoryItem item : inventory) {
            tableModel.addRow(new Object[]{
                item.getIngredientID(),
                item.getName(),
                item.getQuantity()
            });
        }
    }
}
