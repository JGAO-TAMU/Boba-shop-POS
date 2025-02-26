package bobaapp.views;

import bobaapp.database.MenuDAO;
import bobaapp.database.DrinkIngredientsDAO;
import bobaapp.database.InventoryDAO;
import bobaapp.models.MenuItem;
import bobaapp.models.InventoryItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class MenuPanel extends JPanel {
    private JTable menuTable;
    private DefaultTableModel tableModel;

    public MenuPanel() {
        setLayout(new BorderLayout());
        
        // Create table
        String[] columns = {"ID", "Drink Name", "Price"};
        tableModel = new DefaultTableModel(columns, 0);
        menuTable = new JTable(tableModel);
        add(new JScrollPane(menuTable), BorderLayout.CENTER);
        
        // Create buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Drink");
        JButton updateButton = new JButton("Update Price");
        JButton deleteButton = new JButton("Delete Drink");
        JButton ingredientsButton = new JButton("Set Ingredients");
        
        addButton.addActionListener(e -> showAddDrinkDialog());
        updateButton.addActionListener(e -> showUpdatePriceDialog());
        deleteButton.addActionListener(e -> showDeleteDialog());
        ingredientsButton.addActionListener(e -> showIngredientsDialog());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(ingredientsButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        refreshMenuTable();
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

        // show current ingredients
        List<Map<String, Object>> currentIngredients = DrinkIngredientsDAO.getIngredientsForDrink(menuId);
        StringBuilder currentIngredientsText = new StringBuilder("Current ingredients:\n");
        for (Map<String, Object> ingredient : currentIngredients) {
            currentIngredientsText.append(ingredient.get("name"))
                                .append(" (ID: ")
                                .append(ingredient.get("ingredientId"))
                                .append(") - Quantity: ")
                                .append(ingredient.get("quantityUsed"))
                                .append("\n");
        }

        // get available ingredients
        List<InventoryItem> availableIngredients = InventoryDAO.getInventory();
        StringBuilder availableIngredientsText = new StringBuilder("\nAvailable ingredients:\n");
        for (InventoryItem ingredient : availableIngredients) {
            availableIngredientsText.append(ingredient.getName())
                                  .append(" (ID: ")
                                  .append(ingredient.getIngredientID())
                                  .append(")\n");
        }

        // create input panel
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // show both current and available ingredients
        JTextArea ingredientsArea = new JTextArea(
            currentIngredientsText.toString() + 
            "\n----------------------------------------\n" +
            availableIngredientsText.toString(), 
            10, 30);
        ingredientsArea.setEditable(false);
        panel.add(new JScrollPane(ingredientsArea));
        panel.add(new JLabel());
        
        JTextField ingredientIdField = new JTextField();
        JTextField quantityField = new JTextField();
        
        panel.add(new JLabel("Ingredient ID:"));
        panel.add(ingredientIdField);
        panel.add(new JLabel("Quantity Used:"));
        panel.add(quantityField);

        int option = JOptionPane.showConfirmDialog(
            this, panel, 
            "Set Ingredients for " + drinkName,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                int ingredientId = Integer.parseInt(ingredientIdField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());

                if (DrinkIngredientsDAO.addIngredientToDrink(menuId, ingredientId, quantity)) {
                    JOptionPane.showMessageDialog(this, "Ingredient added successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add ingredient.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
            }
        }
    }

    private void refreshMenuTable() {
        tableModel.setRowCount(0);
        List<MenuItem> menuItems = MenuDAO.getMenuItems();
        for (MenuItem item : menuItems) {
            tableModel.addRow(new Object[]{
                item.getId(),
                item.getName(),
                item.getPrice()
            });
        }
    }
}
