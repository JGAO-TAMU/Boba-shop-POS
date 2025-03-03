package bobaapp.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;
import javax.swing.table.*;
import bobaapp.models.*;
import bobaapp.utils.DbUtil;
import java.util.List;
import java.util.ArrayList;

public class ModificationsPanel extends JPanel {
    private bobaapp.models.MenuItem selectedDrink;
    private JLabel titleLabel;
    
    private Color lightYellowBackground = new Color(255, 255, 204);
    private Color lightGrayPanel = new Color(220, 220, 225);
    private Color peachButton = new Color(255, 218, 185);
    private Color mintButton = new Color(162, 236, 236);
    private Color selectedCellColor = new Color(173, 216, 230); // Light blue for selection
    
    // Tables for tracking selections
    private Map<String, JTable> categoryTables = new HashMap<>();
    private Map<String, Set<Point>> categorySelections = new HashMap<>();
    private Map<String, Point> singleSelectionCategories = new HashMap<>();
    
    // Keep track of selected modifications
    private Set<Modification> selectedModifications = new HashSet<>();
    // Store all modifications by category
    private Map<String, List<Modification>> modificationsByCategory = new HashMap<>();
    
    public ModificationsPanel() {
        // Set up the panel
        setLayout(new BorderLayout());
        
        // Create the main content panel with light yellow background
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(lightYellowBackground);
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Options on right
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(Color.LIGHT_GRAY);
        optionsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        // Create central content panel for customization options
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 10));
        contentPanel.setBackground(lightYellowBackground);
        
        // Title at top
        titleLabel = new JLabel("Select a Drink", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Main customization panel
        JPanel customizationPanel = new JPanel();
        customizationPanel.setBackground(lightGrayPanel);
        customizationPanel.setLayout(new BoxLayout(customizationPanel, BoxLayout.Y_AXIS));
        customizationPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Load modifications from the database
        modificationsByCategory = DbUtil.getModificationsByCategory();
        
        // For each category, create a section with modifications
        for (String category : modificationsByCategory.keySet()) {
            // Create header label for the category
            JLabel categoryLabel = new JLabel(category);
            categoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
            categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            customizationPanel.add(categoryLabel);
            customizationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            List<Modification> mods = modificationsByCategory.get(category);
            
            // Create table data
            int totalMods = mods.size();
            int cols = 3; // Number of columns
            int rows = (totalMods + cols - 1) / cols; // Calculate rows needed
            
            String[] columns = new String[cols];
            Object[][] data = new Object[rows][cols];
            
            // Fill the table data with modifications
            for (int i = 0; i < totalMods; i++) {
                int row = i / cols;
                int col = i % cols;
                data[row][col] = mods.get(i);
            }
            
            // Create table for this category
            JTable table = createCustomTable(columns, data);
            
            // Setup selection behavior based on category
            boolean isSingleSelect = "Ice".equals(category) || "Sugar".equals(category);
            
            if (isSingleSelect) {
                // For Ice and Sugar, we allow only one selection
                singleSelectionCategories.put(category, new Point(0, 0)); // Default selection
                categorySelections.put(category, new HashSet<>());
            } else {
                // For other categories like Toppings, allow multiple selections
                categorySelections.put(category, new HashSet<>());
            }
            
            setupTableSelection(table, category);
            categoryTables.put(category, table);
            
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(lightGrayPanel);
            tablePanel.add(table);
            tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            customizationPanel.add(tablePanel);
            
            customizationPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }
        
        JScrollPane scrollPane = new JScrollPane(customizationPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add Drink button at the right
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(lightYellowBackground);
        rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        // Add clickable area for adding drink to order
        rightPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addDrinkToOrder();
            }
        });

        JLabel addDrinkLabel = new JLabel("Add Drink", SwingConstants.CENTER);
        addDrinkLabel.setPreferredSize(new Dimension(120, 60));
        addDrinkLabel.setOpaque(true);
        addDrinkLabel.setBackground(mintButton);
        
        rightPanel.add(addDrinkLabel);
        
        // Add components to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
        
        // Set default selections for categories with single selection
        SwingUtilities.invokeLater(() -> {
            for (String category : singleSelectionCategories.keySet()) {
                JTable table = categoryTables.get(category);
                Point selection = singleSelectionCategories.get(category);
                if (table != null && selection != null) {
                    applySelection(table, selection, true);
                    
                    // Add the default selected modification to selectedModifications
                    if (selection.y < table.getRowCount() && selection.x < table.getColumnCount()) {
                        Object value = table.getValueAt(selection.y, selection.x);
                        if (value instanceof Modification) {
                            selectedModifications.add((Modification) value);
                        }
                    }
                }
            }
        });
    }
    
    public void setDrink(bobaapp.models.MenuItem drink) {
        this.selectedDrink = drink;
        titleLabel.setText(drink.getName() + " - $" + String.format("%.2f", drink.getPrice()));
    }
    
    private void addDrinkToOrder() {
        if (selectedDrink == null) {
            JOptionPane.showMessageDialog(this, "Please select a drink first", "No Drink Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get selected modifications from each category
        List<String> modificationNames = new ArrayList<>();
        
        for (Modification mod : selectedModifications) {
            modificationNames.add(mod.getName());
            // Note: The OrderItem constructor will handle the pricing
        }
        
        // Find specific ice and sugar selections
        String iceLevel = "Regular Ice"; // Default
        String sugarLevel = "100% Sugar"; // Default
        
        for (Modification mod : selectedModifications) {
            String category = mod.getCategory();
            if ("Ice".equals(category)) {
                iceLevel = mod.getName();
            } else if ("Sugar".equals(category)) {
                sugarLevel = mod.getName();
            }
        }
        
        // Create new order item with selections
        OrderItem orderItem = new OrderItem(selectedDrink, iceLevel, sugarLevel, modificationNames);
        CurrentOrder.getInstance().addItem(orderItem);
        
        // Show home panel
        CardLayout cl = (CardLayout) getParent().getLayout();
        cl.show(getParent(), "Checkout");
    }
    
    private JTable createCustomTable(String[] columns, Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setBackground(lightGrayPanel);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(10, 0));
        table.getTableHeader().setVisible(false);
        
        // Remove table border
        table.setBorder(BorderFactory.createEmptyBorder());
        
        // Custom cell renderer for handling selections
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, false, false, row, column);

                // Only render if there's actual content
                if (value == null) {
                    c.setBackground(lightGrayPanel);
                    setText("");
                    return c;
                }
                
                // Get the actual text for display
                if (value instanceof Modification) {
                    Modification mod = (Modification) value;
                    setText(mod.toString());
                    
                    // Determine if selected based on our tracking
                    isSelected = selectedModifications.contains(mod);
                }
                
                if (isSelected) {
                    c.setBackground(selectedCellColor);
                } else {
                    c.setBackground(lightGrayPanel);
                }
                
                return c;
            }
        });
        
        return table;
    }
    
    private void setupTableSelection(JTable table, String category) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
    
                // Only allow selection if cell has content
                if (row >= 0 && col >= 0) {
                    Object cellValue = table.getValueAt(row, col);
                    if (cellValue instanceof Modification) {
                        Modification mod = (Modification) cellValue;
                        
                        if ("Ice".equals(category) || "Sugar".equals(category)) {
                            // Single selection for these categories
                            // Remove any previous selection from this category
                            Iterator<Modification> iterator = selectedModifications.iterator();
                            while (iterator.hasNext()) {
                                Modification selected = iterator.next();
                                if (category.equals(selected.getCategory())) {
                                    iterator.remove();
                                }
                            }
                            
                            // Add the new selection
                            selectedModifications.add(mod);
                            
                            // Update visual selection point
                            singleSelectionCategories.put(category, new Point(col, row));
                        } else {
                            // Toggle selection for multi-select categories
                            if (selectedModifications.contains(mod)) {
                                selectedModifications.remove(mod);
                            } else {
                                selectedModifications.add(mod);
                            }
                        }
                        
                        // Repaint all tables to update selection highlighting
                        for (JTable t : categoryTables.values()) {
                            t.repaint();
                        }
                    }
                }
            }
        });
    }
    
    private void applySelection(JTable table, Point selection, boolean initialSelection) {
        if (selection != null && table != null) {
            // Make sure we're only selecting a valid cell
            if (selection.y < table.getRowCount() && selection.x < table.getColumnCount()) {
                Object cellValue = table.getValueAt(selection.y, selection.x);
                if (cellValue instanceof Modification) {
                    if (initialSelection) {
                        selectedModifications.add((Modification) cellValue);
                    }
                    table.repaint();
                }
            }
        }
    }
    
    
}