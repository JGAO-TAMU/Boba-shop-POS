package bobaapp.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ZReport {
    private Timestamp generatedAt;
    private int totalOrders;
    private double totalSales;
    private double totalTax;
    private List<Order> orders;
    private List<HourlySales> hourlySales;
    private Map<String, Double> paymentMethodTotals;
    private boolean resetCompleted;

    public ZReport(List<Order> todaysOrders, List<HourlySales> hourlySales, 
                 Map<String, Double> paymentMethodTotals, double totalTax, boolean resetCompleted) {
        this.generatedAt = new Timestamp(System.currentTimeMillis());
        this.orders = todaysOrders;
        this.totalOrders = todaysOrders.size();
        this.totalSales = calculateTotalSales(todaysOrders);
        this.hourlySales = hourlySales;
        this.paymentMethodTotals = paymentMethodTotals;
        this.totalTax = totalTax;
        this.resetCompleted = resetCompleted;
    }

    private double calculateTotalSales(List<Order> orders) {
        return orders.stream().mapToDouble(Order::getPrice).sum();
    }

    public String generateReportText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder reportText = new StringBuilder();
        
        reportText.append("========== Z-REPORT ==========\n");
        reportText.append("FINAL DAILY SALES REPORT\n");
        reportText.append("Generated at: ").append(dateFormat.format(generatedAt)).append("\n");
        reportText.append("Report Date: ").append(dateFormat.format(new Date()).substring(0, 10)).append("\n");
        reportText.append("Total Orders: ").append(totalOrders).append("\n");
        reportText.append("Total Sales: $").append(String.format("%.2f", totalSales)).append("\n");
        reportText.append("Total Tax: $").append(String.format("%.2f", totalTax)).append("\n");
        reportText.append("Net Sales: $").append(String.format("%.2f", totalSales - totalTax)).append("\n");
        
        if (resetCompleted) {
            reportText.append("*** DAILY ORDERS HAVE BEEN CLEARED ***\n");
        } else {
            reportText.append("!!! WARNING: DAILY ORDERS WERE NOT CLEARED !!!\n");
        }
        
        reportText.append("============================\n\n");
        reportText.append("NOTE: This is a simplified Z-Report. Order data is not archived.\n");
        reportText.append("      It is recommended to save or print this report for your records.\n\n");
        
        // Payment methods breakdown
        reportText.append("PAYMENT METHODS:\n");
        reportText.append("Method       | Amount\n");
        reportText.append("------------------------\n");
        
        double totalPayments = 0;
        if (paymentMethodTotals.isEmpty()) {
            reportText.append("No payment data available.\n");
        } else {
            for (Map.Entry<String, Double> entry : paymentMethodTotals.entrySet()) {
                reportText.append(String.format("%-12s | $%.2f\n", entry.getKey(), entry.getValue()));
                totalPayments += entry.getValue();
            }
            reportText.append("------------------------\n");
            reportText.append(String.format("TOTAL        | $%.2f\n", totalPayments));
        }
        
        reportText.append("\n============================\n\n");
        
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
        
        reportText.append("\n============================\n");
        reportText.append("CERTIFICATION\n\n");
        reportText.append("Manager Signature: _______________________\n\n");
        reportText.append("Date: ").append(dateFormat.format(new Date()).substring(0, 10)).append("\n");
        reportText.append("============================\n");
        
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
    
    public double getTotalTax() {
        return totalTax;
    }

    public List<Order> getOrders() {
        return orders;
    }
    
    public List<HourlySales> getHourlySales() {
        return hourlySales;
    }
    
    public Map<String, Double> getPaymentMethodTotals() {
        return paymentMethodTotals;
    }
    
    public boolean isResetCompleted() {
        return resetCompleted;
    }
}
