package bobaapp.views;

import bobaapp.database.OrdersDAO;
import bobaapp.models.Order;
import bobaapp.models.XReport;
import bobaapp.models.HourlySales;

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
        
        // Add buttons
        JButton refreshButton = new JButton("Refresh Orders");
        refreshButton.addActionListener(e -> refreshOrderHistory());
        
        JButton xReportButton = new JButton("Generate X-Report");
        xReportButton.addActionListener(e -> generateXReport());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(xReportButton);
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
    
    // Method to generate X-report (daily sales report) with hourly breakdown
    private void generateXReport() {
        // Get today's orders and hourly sales data
        List<Order> todaysOrders = OrdersDAO.getTodaysOrders();
        List<HourlySales> hourlySales = OrdersDAO.getTodaySalesByHour();
        
        if (todaysOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No orders found for today.", 
                "X-Report", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create and display the X-report with hourly breakdown
        XReport report = new XReport(todaysOrders, hourlySales);
        String reportText = report.generateReportText();
        
        // Create a dialog to display the report
        JDialog reportDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                         "X-Report with Hourly Sales", true);
        reportDialog.setLayout(new BorderLayout());
        
        // Create text area for the report
        JTextArea textArea = new JTextArea(reportText);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane textScrollPane = new JScrollPane(textArea);
        
        // Add print and close buttons
        JButton printButton = new JButton("Print Report");
        printButton.addActionListener(e -> printReport(textArea));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> reportDialog.dispose());
        
        // Add button to view as chart
        JButton chartButton = new JButton("View as Chart");
        chartButton.addActionListener(e -> showSalesChart(report.getHourlySales()));
        
        JPanel reportButtonPanel = new JPanel();
        reportButtonPanel.add(printButton);
        reportButtonPanel.add(chartButton);
        reportButtonPanel.add(closeButton);
        
        reportDialog.add(textScrollPane, BorderLayout.CENTER);
        reportDialog.add(reportButtonPanel, BorderLayout.SOUTH);
        reportDialog.setSize(600, 600);
        reportDialog.setLocationRelativeTo(this);
        reportDialog.setVisible(true);
    }
    
    // Method to show sales data as a chart
    private void showSalesChart(List<HourlySales> hourlySales) {
        JDialog chartDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                        "Hourly Sales Chart", true);
        chartDialog.setLayout(new BorderLayout());
        
        // Create panel for the chart
        JPanel chartPanel = new HourlySalesChartPanel(hourlySales);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> chartDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        
        chartDialog.add(chartPanel, BorderLayout.CENTER);
        chartDialog.add(buttonPanel, BorderLayout.SOUTH);
        chartDialog.setSize(800, 500);
        chartDialog.setLocationRelativeTo(this);
        chartDialog.setVisible(true);
    }
    
    // Method to handle printing the report
    private void printReport(JTextArea textArea) {
        try {
            boolean complete = textArea.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, 
                    "X-Report printed successfully!", 
                    "Print", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Printing canceled", 
                    "Print", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error printing: " + e.getMessage(), 
                "Print Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
