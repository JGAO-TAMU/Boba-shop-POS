package bobaapp.views;

import bobaapp.database.ReportDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ManagerHomePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel reportPanel;
    private JPanel revenueTrendChartPanel;
    private int selectedDays = 30;

    public ManagerHomePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 224));

        JTabbedPane tabbedPane = new JTabbedPane();

        // tabbedPane.addTab("Orders Over Time", createOrdersOverTimePanel());
        tabbedPane.addTab("Low Stock Alert", createLowStockAlertPanel());
        tabbedPane.addTab("Revenue Trend", createRevenueTrendPanel());
        tabbedPane.addTab("Ingredient Usage", createIngredientUsagePanel());
        tabbedPane.addTab("Revenue Trend Chart", createRevenueTrendChartPanel());

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

    private JPanel createRevenueTrendChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create a panel for the chart
        revenueTrendChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRevenueChart(g, selectedDays);
            }
        };
        revenueTrendChartPanel.setBackground(Color.WHITE);
        
        
        JPanel controlPanel = new JPanel();
        ButtonGroup timeFrameGroup = new ButtonGroup();
        
        JRadioButton thirtyDaysButton = new JRadioButton("30 Days");
        thirtyDaysButton.setSelected(true);
        thirtyDaysButton.addActionListener(e -> updateRevenueChart(30));
        
        JRadioButton sixtyDaysButton = new JRadioButton("60 Days");
        sixtyDaysButton.addActionListener(e -> updateRevenueChart(60));
        
        JRadioButton ninetyDaysButton = new JRadioButton("90 Days");
        ninetyDaysButton.addActionListener(e -> updateRevenueChart(90));
        
        timeFrameGroup.add(thirtyDaysButton);
        timeFrameGroup.add(sixtyDaysButton);
        timeFrameGroup.add(ninetyDaysButton);
        
        controlPanel.add(thirtyDaysButton);
        controlPanel.add(sixtyDaysButton);
        controlPanel.add(ninetyDaysButton);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(revenueTrendChartPanel), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateRevenueChart(int days) {
        selectedDays = days;
        revenueTrendChartPanel.repaint();
    }
    
    private void drawRevenueChart(Graphics g, int days) {
        if (g == null) return;
        
        Map<String, Double> revenueTrend = ReportDAO.getRevenueTrendByTimeFrame(days);
        if (revenueTrend.isEmpty()) {
            g.drawString("No data available for the selected period", 50, 50);
            return;
        }
        
        // sort the data by date
        Map<String, Double> sortedData = new TreeMap<>(revenueTrend);
        
        int padding = 50;
        int labelPadding = 25;
        int width = revenueTrendChartPanel.getWidth() - 2 * padding;
        int height = revenueTrendChartPanel.getHeight() - 2 * padding;
        
        // find the maximum revenue value
        double maxRevenue = sortedData.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        
        // draw axes
        g.setColor(Color.BLACK);
        g.drawLine(padding, padding, padding, height + padding);
        g.drawLine(padding, height + padding, width + padding, height + padding);
        
        // draw y-axis labels
        int yLabelCount = 5;
        for (int i = 0; i <= yLabelCount; i++) {
            int y = height + padding - i * height / yLabelCount;
            double value = i * maxRevenue / yLabelCount;
            g.drawString(String.format("$%.2f", value), padding - labelPadding, y);
            g.drawLine(padding - 5, y, padding, y);
        }
        
        // draw data points and lines
        g.setColor(Color.BLUE);
        
        String[] dates = sortedData.keySet().toArray(new String[0]);
        int pointWidth = width / (dates.length - 1 > 0 ? dates.length - 1 : 1);
        
        int[] xPoints = new int[dates.length];
        int[] yPoints = new int[dates.length];
        
        for (int i = 0; i < dates.length; i++) {
            String date = dates[i];
            double revenue = sortedData.get(date);
            
            int x = padding + i * pointWidth;
            int y = padding + height - (int)((revenue / maxRevenue) * height);
            
            xPoints[i] = x;
            yPoints[i] = y;
            
            g.fillOval(x - 3, y - 3, 6, 6);
            
            // draw x-axis labels
            if (i % (dates.length > 10 ? dates.length/10 : 1) == 0) {
                // format the date string for display
                String displayDate;
                try {
                    LocalDate parsedDate = LocalDate.parse(date.substring(0, 10));
                    displayDate = parsedDate.format(DateTimeFormatter.ofPattern("MM/dd"));
                } catch (Exception e) {
                    displayDate = date;
                }
                
                g.setColor(Color.BLACK);
                g.drawString(displayDate, x - 15, height + padding + 15);
                g.setColor(Color.BLUE);
            }
        }
        
        // draw connecting lines
        for (int i = 0; i < dates.length - 1; i++) {
            g.drawLine(xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]);
        }
        
        // draw chart title
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Daily Revenue Trend - Last " + days + " Days", padding + 50, padding - 10);
    }
    
    private void generateReport() {
        // create a new frame to display the report
        JFrame reportFrame = new JFrame("Generated Report");
        reportFrame.setSize(800, 600);
        reportFrame.setLayout(new BorderLayout());

        // create a tabbed pane to hold the reports
        JTabbedPane reportTabbedPane = new JTabbedPane();

        // add the reports to the tabbed pane
        // reportTabbedPane.addTab("Orders Over Time", createOrdersOverTimePanel());
        reportTabbedPane.addTab("Low Stock Alert", createLowStockAlertPanel());
        reportTabbedPane.addTab("Revenue Trend", createRevenueTrendPanel());
        reportTabbedPane.addTab("Ingredient Usage", createIngredientUsagePanel());
        reportTabbedPane.addTab("Revenue Trend Chart", createRevenueTrendChartPanel());

        // add the tabbed pane to the frame
        reportFrame.add(reportTabbedPane, BorderLayout.CENTER);

        // display the frame
        reportFrame.setVisible(true);
    }
}