package bobaapp.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class XReport {
    private Timestamp generatedAt;
    private int totalOrders;
    private double totalSales;
    private List<Order> orders;
    private List<HourlySales> hourlySales;

    public XReport(List<Order> todaysOrders, List<HourlySales> hourlySales) {
        this.generatedAt = new Timestamp(System.currentTimeMillis());
        this.orders = todaysOrders;
        this.totalOrders = todaysOrders.size();
        this.totalSales = calculateTotalSales(todaysOrders);
        this.hourlySales = hourlySales;
    }

    private double calculateTotalSales(List<Order> orders) {
        return orders.stream().mapToDouble(Order::getPrice).sum();
    }

    public String generateReportText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder reportText = new StringBuilder();
        
        reportText.append("========== X-REPORT ==========\n");
        reportText.append("Generated at: ").append(dateFormat.format(generatedAt)).append("\n");
        reportText.append("Report Date: ").append(dateFormat.format(new Date()).substring(0, 10)).append("\n");
        reportText.append("Total Orders: ").append(totalOrders).append("\n");
        reportText.append("Total Sales: $").append(String.format("%.2f", totalSales)).append("\n");
        reportText.append("============================\n\n");
        
        // Add hourly sales breakdown
        reportText.append("HOURLY SALES BREAKDOWN:\n");
        reportText.append("Hour         | Orders | Sales\n");
        reportText.append("------------------------\n");
        
        // Only include hours that had sales
        boolean hasActiveHours = false;
        for (HourlySales hourData : hourlySales) {
            if (hourData.getOrderCount() > 0) {
                hasActiveHours = true;
                reportText.append(String.format("%-12s | %-6d | $%.2f\n", 
                    hourData.getFormattedHour(), 
                    hourData.getOrderCount(), 
                    hourData.getTotalSales()));
            }
        }
        
        if (!hasActiveHours) {
            reportText.append("No sales recorded by hour today.\n");
        }
        
        reportText.append("\n============================\n\n");
        
        reportText.append("ORDER DETAILS:\n");
        for (Order order : orders) {
            reportText.append("Order #").append(order.getOrderID())
                     .append(" | ").append(dateFormat.format(order.getTimestamp()))
                     .append(" | $").append(String.format("%.2f", order.getPrice()))
                     .append(" | Employee: ").append(order.getEmployeeName())
                     .append("\n");
        }
        
        return reportText.toString();
    }

    public Timestamp getGeneratedAt() {
        return generatedAt;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public List<Order> getOrders() {
        return orders;
    }
    
    public List<HourlySales> getHourlySales() {
        return hourlySales;
    }
}
