package bobaapp.views;

import bobaapp.database.OrdersDAO;
import bobaapp.database.DatabaseInitializer;
import bobaapp.models.Order;
import bobaapp.models.XReport;
import bobaapp.models.ZReport;
import bobaapp.models.HourlySales;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class OrderHistoryPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable ordersTable;
    
    public OrderHistoryPanel() {
        setLayout(new BorderLayout());
        
        // Simplified initialization - no tables to create
        DatabaseInitializer.initializeZReportTables();
        
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
        
        JButton zReportButton = new JButton("Generate Z-Report");
        zReportButton.addActionListener(e -> generateZReport());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(xReportButton);
        buttonPanel.add(zReportButton);
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
    
    // Simplified Z-report generation that just clears today's orders
    private void generateZReport() {
        // Get today's orders and related data
        List<Order> todaysOrders = OrdersDAO.getTodaysOrders();
        
        if (todaysOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No orders found for today. Cannot generate Z-Report.", 
                "Z-Report", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show warning about Z-Report side effects
        int confirm = JOptionPane.showConfirmDialog(this,
            "WARNING: Z-Report will DELETE all of today's orders from the system.\n\n" +
            "This operation should ONLY be performed at the end of business day.\n" +
            "This action cannot be undone. The orders will be permanently deleted.\n\n" +
            "Are you absolutely sure you want to continue?",
            "Z-Report Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Second confirmation as an additional safeguard
        confirm = JOptionPane.showConfirmDialog(this,
            "FINAL CONFIRMATION:\n\n" +
            "By clicking YES, you confirm that:\n" +
            "1. It is the end of the business day\n" +
            "2. No more orders will be taken today\n" +
            "3. You are authorized to generate the Z-Report\n\n" +
            "Proceed with Z-Report generation and order deletion?",
            "Z-Report Final Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
            
        // Gather all necessary data before we delete
        List<HourlySales> hourlySales = OrdersDAO.getTodaySalesByHour();
        Map<String, Double> paymentTotals = OrdersDAO.getTodaysPaymentMethodTotals();
        double totalTax = OrdersDAO.getTodaysTaxAmount();
        
        // Make a copy of orders before deleting
        List<Order> ordersCopy = new ArrayList<>(todaysOrders);
        
        try {
            // Reset daily sales (simplified to just delete today's orders)
            boolean resetSuccessful = OrdersDAO.resetDailySales();
            
            // Create and display the Z-report
            ZReport report = new ZReport(ordersCopy, hourlySales, paymentTotals, totalTax, resetSuccessful);
            String reportText = report.generateReportText();
            
            // Create a dialog to display the report
            JDialog reportDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                             "Z-Report - End of Day", true);
            reportDialog.setLayout(new BorderLayout());
            
            // Create text area for the report
            JTextArea textArea = new JTextArea(reportText);
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            JScrollPane textScrollPane = new JScrollPane(textArea);
            
            // Add print and close buttons
            JButton printButton = new JButton("Print Z-Report");
            printButton.addActionListener(e -> printReport(textArea));
            
            JButton saveButton = new JButton("Save Report");
            saveButton.addActionListener(e -> saveReportToFile(reportText));
            
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> {
                reportDialog.dispose();
                refreshOrderHistory(); // Refresh to show changes
            });
            
            JPanel reportButtonPanel = new JPanel();
            reportButtonPanel.add(printButton);
            reportButtonPanel.add(saveButton);
            reportButtonPanel.add(closeButton);
            
            reportDialog.add(textScrollPane, BorderLayout.CENTER);
            reportDialog.add(reportButtonPanel, BorderLayout.SOUTH);
            reportDialog.setSize(600, 600);
            reportDialog.setLocationRelativeTo(this);
            reportDialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error generating Z-Report: " + e.getMessage(),
                "Z-Report Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
                    "Report printed successfully!", 
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
    
    // New method to save report to a file
    private void saveReportToFile(String reportText) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Z-Report");
        
        java.util.Date today = new java.util.Date();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String defaultFileName = "Z-Report_" + dateFormat.format(today) + ".txt";
        fileChooser.setSelectedFile(new java.io.File(defaultFileName));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave)) {
                writer.print(reportText);
                JOptionPane.showMessageDialog(this, 
                    "Z-Report saved successfully to: " + fileToSave.getAbsolutePath(), 
                    "File Saved", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving file: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}
