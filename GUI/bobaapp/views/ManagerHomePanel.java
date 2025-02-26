package bobaapp.views;

import bobaapp.database.ReportDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class ManagerHomePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel reportPanel;

    public ManagerHomePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 224));

        JTabbedPane tabbedPane = new JTabbedPane();

        // tabbedPane.addTab("Orders Over Time", createOrdersOverTimePanel());
        tabbedPane.addTab("Low Stock Alert", createLowStockAlertPanel());
        tabbedPane.addTab("Revenue Trend", createRevenueTrendPanel());
        tabbedPane.addTab("Ingredient Usage", createIngredientUsagePanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Add Generate Report button
        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        add(generateReportButton, BorderLayout.SOUTH);
    }

    // private JPanel createOrdersOverTimePanel() {
    //     JPanel panel = new JPanel(new BorderLayout());

    //     // Create table model
    //     DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Date", "Menu Item", "Order Count"}, 0);
    //     JTable table = new JTable(tableModel);

    //     // Populate table model
    //     Map<String, Map<String, Integer>> ordersOverTime = ReportDAO.getOrdersOverTime();
    //     for (String menuItem : ordersOverTime.keySet()) {
    //         for (String date : ordersOverTime.get(menuItem).keySet()) {
    //             tableModel.addRow(new Object[]{date, menuItem, ordersOverTime.get(menuItem).get(date)});
    //         }
    //     }

    //     // Add table to scroll pane
    //     JScrollPane scrollPane = new JScrollPane(table);
    //     panel.add(scrollPane, BorderLayout.CENTER);

    //     return panel;
    // }

    private JPanel createLowStockAlertPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        List<String> lowStockItems = ReportDAO.getLowStockItems();
        for (String item : lowStockItems) {
            textArea.append(item + "\n");
        }

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRevenueTrendPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create table model
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Date", "Revenue"}, 0);
        JTable table = new JTable(tableModel);

        // Populate table model
        Map<String, Double> revenueTrend = ReportDAO.getRevenueTrend();
        for (String date : revenueTrend.keySet()) {
            tableModel.addRow(new Object[]{date, revenueTrend.get(date)});
        }

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createIngredientUsagePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        Map<String, Integer> ingredientUsage = ReportDAO.getIngredientUsage();
        for (String ingredient : ingredientUsage.keySet()) {
            textArea.append(ingredient + ": " + ingredientUsage.get(ingredient) + "\n");
        }

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        return panel;
    }

    private void generateReport() {
        // Create a new frame to display the report
        JFrame reportFrame = new JFrame("Generated Report");
        reportFrame.setSize(800, 600);
        reportFrame.setLayout(new BorderLayout());

        // Create a tabbed pane to hold the reports
        JTabbedPane reportTabbedPane = new JTabbedPane();

        // Add the reports to the tabbed pane
        // reportTabbedPane.addTab("Orders Over Time", createOrdersOverTimePanel());
        reportTabbedPane.addTab("Low Stock Alert", createLowStockAlertPanel());
        reportTabbedPane.addTab("Revenue Trend", createRevenueTrendPanel());
        reportTabbedPane.addTab("Ingredient Usage", createIngredientUsagePanel());

        // Add the tabbed pane to the frame
        reportFrame.add(reportTabbedPane, BorderLayout.CENTER);

        // Display the frame
        reportFrame.setVisible(true);
    }
}