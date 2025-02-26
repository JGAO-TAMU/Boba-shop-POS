package bobaapp.views;

import bobaapp.database.OrdersDAO;
import bobaapp.models.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderHistoryPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable ordersTable;
    
    public OrderHistoryPanel() {
        setLayout(new BorderLayout());
        
        // Create table model
        String[] columns = {"Order ID", "Timestamp", "Employee", "Price"};
        tableModel = new DefaultTableModel(columns, 0);
        ordersTable = new JTable(tableModel);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add refresh button
        JButton refreshButton = new JButton("Refresh Orders");
        refreshButton.addActionListener(e -> refreshOrderHistory());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Initial load of order data
        refreshOrderHistory();
    }
    
    // Public method to refresh order history
    public void refreshOrderHistory() {
        tableModel.setRowCount(0);
        List<Order> orders = OrdersDAO.getOrders();
        
        for (Order order : orders) {
            tableModel.addRow(new Object[]{
                order.getOrderID(),
                order.getTimestamp(),
                order.getEmployeeName(),
                String.format("$%.2f", order.getPrice())
            });
        }
    }
}
