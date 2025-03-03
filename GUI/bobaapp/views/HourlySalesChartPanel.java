package bobaapp.views;

import bobaapp.models.HourlySales;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HourlySalesChartPanel extends JPanel {
    private List<HourlySales> hourlySales;
    private Color barColor = new Color(65, 105, 225);
    private Color labelColor = new Color(50, 50, 50);
    private int padding = 50;
    private int labelPadding = 25;
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

    public HourlySalesChartPanel(List<HourlySales> hourlySales) {
        this.hourlySales = hourlySales;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Filter out only hours with sales
        double maxSales = hourlySales.stream()
            .mapToDouble(HourlySales::getTotalSales)
            .max()
            .orElse(100.0); // Default if no sales
            
        // Add 20% padding to max for better visualization
        maxSales = maxSales * 1.2;

        // Calculate dimensions
        int width = getWidth();
        int height = getHeight();
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;

        // Draw Y-axis and labels
        g2.setColor(labelColor);
        g2.drawLine(padding, padding, padding, height - padding);
        
        // Y-axis labels (sales amounts)
        int numYLabels = 5;
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i <= numYLabels; i++) {
            double y = padding + ((numYLabels - i) * chartHeight / numYLabels);
            double salesAmount = (i * maxSales) / numYLabels;
            g2.drawLine(padding - 5, (int)y, padding, (int)y);
            g2.drawString(String.format("$%.2f", salesAmount), padding - 45, (int)y + 5);
        }

        // Draw X-axis and labels
        g2.drawLine(padding, height - padding, width - padding, height - padding);
        
        // Determine which hours to show based on screen space
        boolean showAllHours = width > 600; // Only show all hours if screen is wide enough
        int hourIncrement = showAllHours ? 1 : 3;
        
        // X-axis labels (hours)
        int barWidth = chartWidth / 24;
        for (int i = 0; i < hourlySales.size(); i += hourIncrement) {
            HourlySales hourData = hourlySales.get(i);
            int x = padding + (i * barWidth) + barWidth/2;
            g2.drawLine(x, height - padding, x, height - padding + 5);
            g2.drawString(hourData.getHour() + "h", x - 5, height - padding + 20);
        }

        // Draw the title
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Today's Hourly Sales", width/2 - 80, 30);
        
        // Draw the bars
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < hourlySales.size(); i++) {
            HourlySales hourData = hourlySales.get(i);
            int x = padding + (i * barWidth);
            
            if (hourData.getTotalSales() > 0) {
                double barHeightRatio = hourData.getTotalSales() / maxSales;
                int barHeight = (int) (chartHeight * barHeightRatio);
                g2.setColor(barColor);
                g2.fillRect(x + 4, height - padding - barHeight, barWidth - 8, barHeight);
                g2.setColor(Color.BLACK);
                g2.drawRect(x + 4, height - padding - barHeight, barWidth - 8, barHeight);
                
                // Draw the value on top of the bar if it's tall enough
                if (barHeight > 15) {
                    g2.setColor(Color.WHITE);
                    String salesValue = String.format("$%.2f", hourData.getTotalSales());
                    g2.setFont(new Font("Arial", Font.BOLD, 9));
                    FontMetrics metrics = g2.getFontMetrics();
                    int valueWidth = metrics.stringWidth(salesValue);
                    g2.drawString(salesValue, x + (barWidth - valueWidth)/2, height - padding - barHeight + 12);
                }
            }
        }
    }
}
