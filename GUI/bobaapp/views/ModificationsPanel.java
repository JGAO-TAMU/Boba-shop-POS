package bobaapp.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
import bobaapp.models.*;

public class ModificationsPanel extends JPanel {
    private bobaapp.models.MenuItem selectedDrink;
    private JLabel titleLabel; // Add field declaration
    
    private Color lightYellowBackground = new Color(255, 255, 204);
    private Color lightGrayPanel = new Color(220, 220, 225);
    private Color peachButton = new Color(255, 218, 185);
    private Color mintButton = new Color(162, 236, 236);
    private Color selectedCellColor = new Color(173, 216, 230); // Light blue for selection
    
    // Tables for tracking selections
    private JTable iceTable;
    private JTable sugarTable;
    private JTable toppingsTable;
    
    // For tracking selected cells
    private Point iceSelection = new Point(0, 0); // Default: Regular Ice
    private Point sugarSelection = new Point(0, 0); // Default: 100% Sugar
    private Set<Point> toppingsSelection = new HashSet<>();
    
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
        titleLabel = new JLabel("Select a Drink", SwingConstants.CENTER); // Initialize titleLabel
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Main customization panel
        JPanel customizationPanel = new JPanel();
        customizationPanel.setBackground(lightGrayPanel);
        customizationPanel.setLayout(new BoxLayout(customizationPanel, BoxLayout.Y_AXIS));
        customizationPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create Ice Level section with table
        JLabel iceLevelLabel = new JLabel("Ice Level");
        iceLevelLabel.setFont(new Font("Arial", Font.BOLD, 16));
        iceLevelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customizationPanel.add(iceLevelLabel);
        customizationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Ice level table
        String[] iceColumns = {"", "", ""};
        Object[][] iceData = {
            {"Regular Ice", "Light Ice", ""},
            {"No Ice", "Extra Ice", ""}
        };
        iceTable = createCustomTable(iceColumns, iceData);
        setupTableSelection(iceTable, "ice");
        JPanel iceTablePanel = new JPanel(new BorderLayout());
        iceTablePanel.setBackground(lightGrayPanel);
        iceTablePanel.add(iceTable);
        iceTablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customizationPanel.add(iceTablePanel);
        
        customizationPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Create Sugar Level section with table
        JLabel sugarLevelLabel = new JLabel("Sugar Level");
        sugarLevelLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sugarLevelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customizationPanel.add(sugarLevelLabel);
        customizationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Sugar level table
        String[] sugarColumns = {"", "", ""};
        Object[][] sugarData = {
            {"100% Sugar", "75% Sugar", ""},
            {"50% Sugar", "No Sugar", ""}
        };
        sugarTable = createCustomTable(sugarColumns, sugarData);
        setupTableSelection(sugarTable, "sugar");
        JPanel sugarTablePanel = new JPanel(new BorderLayout());
        sugarTablePanel.setBackground(lightGrayPanel);
        sugarTablePanel.add(sugarTable);
        sugarTablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customizationPanel.add(sugarTablePanel);
        
        customizationPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Create Toppings section with table
        JLabel toppingsLabel = new JLabel("Toppings");
        toppingsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        toppingsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customizationPanel.add(toppingsLabel);
        customizationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Toppings table
        String[] toppingsColumns = {"", "", ""};
        Object[][] toppingsData = {
            {"Boba", "Mini Pearls", "Strawberry Boba"},
            {"Aloe Vera", "Coffee Jelly", "Coconut Jelly"},
            {"Egg Pudding", "Mango Jelly", "Grass Jelly"},
            {"Almond Pudding", "Lychee", "White Pearls"}
        };
        toppingsTable = createCustomTable(toppingsColumns, toppingsData);
        setupTableSelection(toppingsTable, "toppings");
        JPanel toppingsTablePanel = new JPanel(new BorderLayout());
        toppingsTablePanel.setBackground(lightGrayPanel);
        toppingsTablePanel.add(toppingsTable);
        toppingsTablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customizationPanel.add(toppingsTablePanel);
        
        JScrollPane scrollPane = new JScrollPane(customizationPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add Drink button at the right
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(lightYellowBackground);
        rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        // Remove the button and add a MouseListener to the panel
        rightPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addDrinkToOrder();
            }
        });

        // Optionally, you can add a label to indicate the clickable area
        JLabel addDrinkLabel = new JLabel("Add Drink", SwingConstants.CENTER);
        addDrinkLabel.setPreferredSize(new Dimension(120, 60));
        addDrinkLabel.setOpaque(true);
        addDrinkLabel.setBackground(mintButton);
        addDrinkLabel.setBorder(new RoundedBorder(20));
        rightPanel.add(addDrinkLabel);
        
        // Add components to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
        
        // Set default selections for ice and sugar level
        SwingUtilities.invokeLater(() -> {
            // Apply initial selections
            applySelection(iceTable, iceSelection, true);
            applySelection(sugarTable, sugarSelection, true);
        });
    }
    
    public void setDrink(bobaapp.models.MenuItem drink) {
        this.selectedDrink = drink;
        titleLabel.setText(drink.getName() + " - $" + String.format("%.2f", drink.getPrice()));
    }
    
    private void addDrinkToOrder() {
        // Get selected modifications
        String iceLevel = (String) iceTable.getValueAt(iceSelection.y, iceSelection.x);
        String sugarLevel = (String) sugarTable.getValueAt(sugarSelection.y, sugarSelection.x);
        
        // Get selected toppings
        List<String> selectedToppings = new ArrayList<>();
        for (Point p : toppingsSelection) {
            String topping = (String) toppingsTable.getValueAt(p.y, p.x);
            if (topping != null && !topping.isEmpty()) {
                selectedToppings.add(topping);
            }
        }
        
        // Create new order item with quantities
        OrderItem orderItem = new OrderItem(selectedDrink, iceLevel, sugarLevel, selectedToppings);
        CurrentOrder.getInstance().addItem(orderItem);
        
        // Show checkout panel
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

                // Check if this is the selected cell (handled by our custom selection logic)
                Point selection = new Point(column, row);
                if (table == iceTable) {
                    isSelected = iceSelection != null && iceSelection.equals(selection);
                } else if (table == sugarTable) {
                    isSelected = sugarSelection != null && sugarSelection.equals(selection);
                } else if (table == toppingsTable) {
                    isSelected = toppingsSelection.contains(selection); // Check if it's in the set
                }

                if (isSelected) {
                    c.setBackground(selectedCellColor);
                } else {
                    c.setBackground(lightGrayPanel);
                }

                // Only enable cell if it has content
                c.setEnabled(value != null && !value.toString().isEmpty());

                return c;
            }
        });
        
        return table;
    }
    
    private void setupTableSelection(JTable table, String tableType) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
    
                // Only allow selection if cell has content
                if (row >= 0 && col >= 0) {
                    Object cellValue = table.getValueAt(row, col);
                    if (cellValue != null && !cellValue.toString().isEmpty()) {
                        Point newSelection = new Point(col, row);
    
                        if (tableType.equals("ice")) {
                            iceSelection = newSelection;
                        } else if (tableType.equals("sugar")) {
                            sugarSelection = newSelection;
                        } else if (tableType.equals("toppings")) {
                            if (toppingsSelection.contains(newSelection)) {
                                // Deselect if already selected
                                toppingsSelection.remove(newSelection);
                            } else {
                                // Add new selection
                                toppingsSelection.add(newSelection);
                            }
                        }
    
                        // Repaint all tables to update selection highlighting
                        iceTable.repaint();
                        sugarTable.repaint();
                        toppingsTable.repaint();
                    }
                }
            }
        });
    }
    
    private void applySelection(JTable table, Point selection, boolean initialSelection) {
        if (selection != null && table != null) {
            // Make sure we're only selecting a valid cell
            Object cellValue = table.getValueAt(selection.y, selection.x);
            if (cellValue != null && !cellValue.toString().isEmpty()) {
                table.repaint();
            }
        }
    }
    
    // Custom rounded border class
    private static class RoundedBorder implements Border {
        private int radius;
        
        RoundedBorder(int radius) {
            this.radius = radius;
        }
        
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }
        
        public boolean isBorderOpaque() {
            return true;
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }
}