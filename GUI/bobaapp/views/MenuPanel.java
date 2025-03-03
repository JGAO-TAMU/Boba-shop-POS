package bobaapp.views;

import bobaapp.database.MenuDAO;
import bobaapp.database.DrinkIngredientsDAO;
import bobaapp.database.InventoryDAO;
import bobaapp.models.MenuItem;
import bobaapp.models.InventoryItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class MenuPanel extends JPanel {
    public interface MenuItemSelectionListener {
        void onMenuItemSelected(MenuItem item);
    }
    
    private JTable menuTable;
    private DefaultTableModel tableModel;
    private List<MenuItemSelectionListener> selectionListeners = new ArrayList<>();
    private ModificationsPanel modificationsPanel;
    
    public MenuPanel() {
        setLayout(new BorderLayout());
        
        String[] columns = {"ID", "Drink Name", "Price"};
        tableModel = new DefaultTableModel(columns, 0);
        menuTable = new JTable(tableModel);
        add(new JScrollPane(menuTable), BorderLayout.CENTER);
        
        menuTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = menuTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int menuId = (int) menuTable.getValueAt(selectedRow, 0);
                        String name = (String) menuTable.getValueAt(selectedRow, 1);
                        double price = Double.parseDouble(menuTable.getValueAt(selectedRow, 2).toString());
                        
                        MenuItem selectedItem = new MenuItem(menuId, name, price);
                        
                        for (MenuItemSelectionListener listener : selectionListeners) {
                            listener.onMenuItemSelected(selectedItem);
                        }
                    }
                }
            }
        });
        
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Drink");
        JButton updateButton = new JButton("Update Price");
        JButton deleteButton = new JButton("Delete Drink");
        JButton ingredientsButton = new JButton("Set Ingredients");
        JButton selectButton = new JButton("Select Drink");
        
        addButton.addActionListener(e -> showAddDrinkDialog());
        updateButton.addActionListener(e -> showUpdatePriceDialog());
        deleteButton.addActionListener(e -> showDeleteDialog());
        ingredientsButton.addActionListener(e -> showIngredientsDialog());
        selectButton.addActionListener(e -> selectCurrentDrink());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(ingredientsButton);
        buttonPanel.add(selectButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        refreshMenuTable();
    }

    // method to add listener for menu item selection
    public void addMenuItemSelectionListener(MenuItemSelectionListener listener) {
        selectionListeners.add(listener);
    }
    
    // method to set ModificationsPanel (for backward compatibility)
    public void setModificationsPanel(ModificationsPanel panel) {
        this.modificationsPanel = panel;
    }
    
    private void selectCurrentDrink() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow >= 0) {
            int menuId = (int) menuTable.getValueAt(selectedRow, 0);
            String name = (String) menuTable.getValueAt(selectedRow, 1);
            double price = Double.parseDouble(menuTable.getValueAt(selectedRow, 2).toString());
            
            MenuItem selectedItem = new MenuItem(menuId, name, price);
            
            // notify all listeners
            for (MenuItemSelectionListener listener : selectionListeners) {
                listener.onMenuItemSelected(selectedItem);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a drink first.");
        }
    }
    
    private void showAddDrinkDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        
        panel.add(new JLabel("Drink Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Add New Drink", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a drink name.");
                    return;
                }

                if (MenuDAO.addMenuItem(name, price)) {
                    refreshMenuTable();
                    JOptionPane.showMessageDialog(this, "Drink added successfully.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price.");
            }
        }
    }

    private void showUpdatePriceDialog() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a drink to update.");
            return;
        }

        int itemId = (int) menuTable.getValueAt(selectedRow, 0);
        String drinkName = (String) menuTable.getValueAt(selectedRow, 1);
        
        String input = JOptionPane.showInputDialog(this,
            "Enter new price for " + drinkName + ":",
            "Update Price",
            JOptionPane.PLAIN_MESSAGE);
            
        if (input != null && !input.trim().isEmpty()) {
            try {
                double newPrice = Double.parseDouble(input);
                if (MenuDAO.updatePrice(itemId, newPrice)) {
                    refreshMenuTable();
                    JOptionPane.showMessageDialog(this, "Price updated successfully.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price.");
            }
        }
    }

    private void showDeleteDialog() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a drink to delete.");
            return;
        }

        int itemId = (int) menuTable.getValueAt(selectedRow, 0);
        String drinkName = (String) menuTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete " + drinkName + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (MenuDAO.deleteMenuItem(itemId)) {
                refreshMenuTable();
                JOptionPane.showMessageDialog(this, "Drink deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete drink.");
            }
        }
    }

    private void showIngredientsDialog() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a drink first.");
            return;
        }

        int menuId = (int) menuTable.getValueAt(selectedRow, 0);
        String drinkName = (String) menuTable.getValueAt(selectedRow, 1);

        // Get ingredients for the selected drink
        List<Map<String, Object>> currentIngredients = DrinkIngredientsDAO.getIngredientsForDrink(menuId);
        
        // Create a tabbed pane for different operations
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: View current ingredients
        JPanel viewPanel = new JPanel(new BorderLayout(10, 10));
        viewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title for current ingredients
        JLabel titleLabel = new JLabel("Current Ingredients for " + drinkName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        DefaultTableModel ingredientModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Quantity Used"}, 0);
        
        for (Map<String, Object> ingredient : currentIngredients) {
            ingredientModel.addRow(new Object[]{
                ingredient.get("ingredientId"),
                ingredient.get("name"),
                ingredient.get("quantityUsed")
            });
        }
        
        JTable ingredientTable = new JTable(ingredientModel);
        ingredientTable.setRowHeight(25);
        ingredientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        ingredientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ingredientTable.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(ingredientTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JButton removeButton = new JButton("Remove Selected Ingredient");
        removeButton.setBackground(new Color(255, 102, 102));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        
        removeButton.addActionListener(e -> {
            int row = ingredientTable.getSelectedRow();
            if (row != -1) {
                int ingredientId = (int) ingredientTable.getValueAt(row, 0);
                
                if (DrinkIngredientsDAO.removeIngredientFromDrink(menuId, ingredientId)) {
                    JOptionPane.showMessageDialog(viewPanel, "Ingredient removed successfully.");
                    // Refresh the table
                    ingredientModel.removeRow(row);
                } else {
                    JOptionPane.showMessageDialog(viewPanel, "Failed to remove ingredient.");
                }
            } else {
                JOptionPane.showMessageDialog(viewPanel, "Please select an ingredient to remove.");
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(removeButton);
        
        viewPanel.add(titleLabel, BorderLayout.NORTH);
        viewPanel.add(scrollPane, BorderLayout.CENTER);
        viewPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel addPanel = new JPanel(new BorderLayout(10, 10));
        addPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel addTitleLabel = new JLabel("Add Ingredients to " + drinkName);
        addTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        addTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        List<InventoryItem> availableIngredients = InventoryDAO.getInventory();
        DefaultTableModel availableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Current Stock"}, 0);
        
        for (InventoryItem ingredient : availableIngredients) {
            availableModel.addRow(new Object[]{
                ingredient.getIngredientId(),
                ingredient.getName(),
                ingredient.getQuantity()
            });
        }
        
        JTable availableTable = new JTable(availableModel);
        availableTable.setRowHeight(25);
        availableTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        availableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableTable.setFillsViewportHeight(true);
        
        JScrollPane availableScrollPane = new JScrollPane(availableTable);
        availableScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField("1", 5);
        quantityField.setHorizontalAlignment(JTextField.CENTER);
        
        JButton addIngredientButton = new JButton("Add Selected Ingredient");
        addIngredientButton.setBackground(new Color(100, 180, 100));
        addIngredientButton.setForeground(Color.WHITE);
        addIngredientButton.setFocusPainted(false);
        
        addIngredientButton.addActionListener(e -> {
            int row = availableTable.getSelectedRow();
            if (row != -1) {
                try {
                    int ingredientId = (int) availableTable.getValueAt(row, 0);
                    int quantity = Integer.parseInt(quantityField.getText());
                    
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(addPanel, "Quantity must be greater than zero.");
                        return;
                    }
                    
                    if (DrinkIngredientsDAO.addIngredientToDrink(menuId, ingredientId, quantity)) {
                        JOptionPane.showMessageDialog(addPanel, "Ingredient added successfully.");
                        
                        List<Map<String, Object>> updatedIngredients = 
                            DrinkIngredientsDAO.getIngredientsForDrink(menuId);
                        
                        ingredientModel.setRowCount(0);
                        for (Map<String, Object> ingredient : updatedIngredients) {
                            ingredientModel.addRow(new Object[]{
                                ingredient.get("ingredientId"),
                                ingredient.get("name"),
                                ingredient.get("quantityUsed")
                            });
                        }
                    } else {
                        JOptionPane.showMessageDialog(addPanel, "Failed to add ingredient.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addPanel, "Please enter a valid quantity.");
                }
            } else {
                JOptionPane.showMessageDialog(addPanel, "Please select an ingredient to add.");
            }
        });
        
        controlPanel.add(quantityLabel);
        controlPanel.add(quantityField);
        controlPanel.add(addIngredientButton);
        
        addPanel.add(addTitleLabel, BorderLayout.NORTH);
        addPanel.add(availableScrollPane, BorderLayout.CENTER);
        addPanel.add(controlPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Current Ingredients", viewPanel);
        tabbedPane.addTab("Add Ingredient", addPanel);
        
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                     "Ingredients for " + drinkName, true);
        dialog.setLayout(new BorderLayout());
        dialog.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel closePanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        closePanel.add(closeButton);
        dialog.add(closePanel, BorderLayout.SOUTH);
        
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void refreshMenuTable() {
        tableModel.setRowCount(0);
        List<MenuItem> menuItems = MenuDAO.getMenuItems();
        for (MenuItem item : menuItems) {
            tableModel.addRow(new Object[]{
                item.getId(),
                item.getName(),
                String.format("%.2f", item.getPrice())
            });
        }
    }
}
