package bobaapp.views;

import bobaapp.database.ReportDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
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
    private JPanel salesReportPanel;
    private DefaultTableModel salesTableModel;
    private Color accentColor = new Color(70, 130, 180); // Steel blue
    private Color bgColor = new Color(240, 248, 255); // Alice blue

    public ManagerHomePanel() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // Add logout button to top right
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(bgColor);
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(accentColor);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton);
        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(245, 245, 250));
        
        // tabbedPane.addTab("Orders Over Time", createOrdersOverTimePanel());
        tabbedPane.addTab("Low Stock Alert", createLowStockAlertPanel());
        tabbedPane.addTab("Revenue Trend", createRevenueTrendPanel());
        tabbedPane.addTab("Ingredient Usage", createIngredientUsagePanel());
        tabbedPane.addTab("Revenue Trend Chart", createRevenueTrendChartPanel());
        tabbedPane.addTab("Sales Report", createSalesReportPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Add Generate Report button
        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.setBackground(accentColor);
        generateReportButton.setForeground(Color.WHITE);
        generateReportButton.setFocusPainted(false);
        generateReportButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(bgColor);
        buttonPanel.add(generateReportButton);
        add(buttonPanel, BorderLayout.SOUTH);
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
    
    private JPanel createSalesReportPanel() {
        salesReportPanel = new JPanel(new BorderLayout(10, 10));
        salesReportPanel.setBackground(bgColor);
        salesReportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(bgColor);
        JLabel titleLabel = new JLabel("Sales Report by Item", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(accentColor);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Create time frame selection panel
        JPanel timeFramePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timeFramePanel.setBackground(bgColor);
        
        JLabel timeFrameLabel = new JLabel("Select Time Period:");
        timeFrameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        String[] timeOptions = {"Last 7 Days", "Last 30 Days", "Last 90 Days"};
        JComboBox<String> timeFrameCombo = new JComboBox<>(timeOptions);
        timeFrameCombo.setSelectedIndex(1); // Default to 30 days
        
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setBackground(accentColor);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        
        timeFramePanel.add(timeFrameLabel);
        timeFramePanel.add(timeFrameCombo);
        timeFramePanel.add(refreshButton);
        
        titlePanel.add(timeFramePanel, BorderLayout.SOUTH);
        salesReportPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Create table for sales data
        salesTableModel = new DefaultTableModel(
            new Object[]{"Rank", "Item Name", "Quantity Sold", "% of Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable salesTable = new JTable(salesTableModel);
        salesTable.setRowHeight(30);
        salesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        salesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        salesTable.getTableHeader().setBackground(accentColor);
        salesTable.getTableHeader().setForeground(Color.WHITE);
        salesTable.setShowGrid(true);
        salesTable.setGridColor(new Color(230, 230, 230));
        
        // Custom cell renderer for percentage column
        salesTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(JLabel.RIGHT);
            }
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setText(value + "%");
                return c;
            }
        });
        
        // Set column widths
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        salesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        salesReportPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create visual summary panel
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(bgColor);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JPanel visualPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSalesBarChart(g);
            }
        };
        visualPanel.setBackground(Color.WHITE);
        visualPanel.setPreferredSize(new Dimension(600, 200));
        visualPanel.setBorder(BorderFactory.createLineBorder(accentColor));
        
        summaryPanel.add(visualPanel, BorderLayout.CENTER);
        salesReportPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Add action listener to refresh button
        refreshButton.addActionListener(e -> {
            int days = 30; // Default
            
            switch (timeFrameCombo.getSelectedIndex()) {
                case 0: days = 7; break;
                case 1: days = 30; break; 
                case 2: days = 90; break;
            }
            
            updateSalesReport(days);
        });
        
        // Initialize with 30-day data
        updateSalesReport(30);
        
        return salesReportPanel;
    }
    
    private void updateSalesReport(int days) {
        // Get sales data from the database
        Map<String, Integer> salesData = ReportDAO.getSalesByItem(days);
        
        // Clear existing data
        salesTableModel.setRowCount(0);
        
        // Calculate total sales
        int totalSales = salesData.values().stream().mapToInt(Integer::intValue).sum();
        
        // Add rows to table
        int rank = 1;
        for (Map.Entry<String, Integer> entry : salesData.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();
            double percentage = totalSales > 0 ? (double) quantity / totalSales * 100 : 0;
            
            salesTableModel.addRow(new Object[]{
                rank++,
                itemName,
                quantity,
                Math.round(percentage * 10) / 10.0 // Round to 1 decimal place
            });
        }
        
        // Refresh the visual panel
        if (salesReportPanel != null) {
            salesReportPanel.repaint();
        }
    }
    
    private void drawSalesBarChart(Graphics g) {
        if (g == null || salesTableModel.getRowCount() == 0) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int padding = 40;
        int barPadding = 10;
        int labelWidth = 120;
        int width = salesReportPanel.getWidth() - padding * 2 - labelWidth;
        int height = 200 - padding * 2;
        
        // Set title
        g2d.setColor(accentColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Top 5 Selling Items", padding, 25);
        
        // Draw coordinate system
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding + labelWidth, padding, padding + labelWidth, padding + height);
        g2d.drawLine(padding + labelWidth, padding + height, padding + labelWidth + width, padding + height);
        
        // Determine the maximum value for scaling
        int maxValue = 0;
        for (int i = 0; i < Math.min(5, salesTableModel.getRowCount()); i++) {
            int value = Integer.parseInt(salesTableModel.getValueAt(i, 2).toString());
            if (value > maxValue) maxValue = value;
        }
        
        // Add 20% padding to the max value for better visualization
        maxValue = (int) (maxValue * 1.2);
        
        // Draw scale markers on y-axis
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i <= 5; i++) {
            int y = padding + height - (i * height / 5);
            int value = i * maxValue / 5;
            g2d.drawString(Integer.toString(value), padding + labelWidth - 25, y + 5);
            g2d.drawLine(padding + labelWidth - 5, y, padding + labelWidth, y);
        }
        
        // Calculate bar width
        int availableWidth = width - (barPadding * (Math.min(5, salesTableModel.getRowCount()) + 1));
        int barWidth = availableWidth / Math.min(5, salesTableModel.getRowCount());
        
        // Draw bars for top 5 items
        for (int i = 0; i < Math.min(5, salesTableModel.getRowCount()); i++) {
            String name = salesTableModel.getValueAt(i, 1).toString();
            int sales = Integer.parseInt(salesTableModel.getValueAt(i, 2).toString());
            double percentage = Double.parseDouble(salesTableModel.getValueAt(i, 3).toString());
            
            // Calculate bar height proportional to sales
            int barHeight = (int) (((double) sales / maxValue) * height);
            
            // Calculate x position
            int x = padding + labelWidth + barPadding + (barWidth + barPadding) * i;
            
            // Calculate y position (starting from bottom)
            int y = padding + height - barHeight;
            
            // Draw bar with gradient
            GradientPaint gradient = new GradientPaint(
                x, y, new Color(100, 180, 255),
                x, y + barHeight, new Color(70, 130, 180)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(x, y, barWidth, barHeight);
            
            // Draw border
            g2d.setColor(new Color(60, 120, 170));
            g2d.drawRect(x, y, barWidth, barHeight);
            
            // Draw item name (shortened if needed)
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            
            // Shorten name if too long
            String displayName = name;
            if (name.length() > 10) {
                displayName = name.substring(0, 8) + "...";
            }
            
            // Rotate text for item names
            AffineTransform oldTransform = g2d.getTransform();
            g2d.rotate(-Math.PI/2);
            g2d.drawString(displayName, -padding - height + 5, x + barWidth/2 + 4);
            g2d.setTransform(oldTransform);
            
            // Draw sales value above bar
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            String salesText = sales + " (" + percentage + "%)";
            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = metrics.stringWidth(salesText);
            g2d.drawString(salesText, x + barWidth/2 - textWidth/2, y - 5);
        }
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
        reportTabbedPane.addTab("Sales Report", createSalesReportPanel());

        // add the tabbed pane to the frame
        reportFrame.add(reportTabbedPane, BorderLayout.CENTER);

        // display the frame
        reportFrame.setVisible(true);
    }

    // method to handle logout
    private void logout() {
        // close the current frame
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        currentFrame.dispose();
        
        // open the login frame
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}