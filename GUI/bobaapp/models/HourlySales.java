package bobaapp.models;

public class HourlySales {
    private int hour;
    private double totalSales;
    private int orderCount;

    public HourlySales(int hour, double totalSales, int orderCount) {
        this.hour = hour;
        this.totalSales = totalSales;
        this.orderCount = orderCount;
    }

    public int getHour() {
        return hour;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public int getOrderCount() {
        return orderCount;
    }
    
    public String getFormattedHour() {
        return String.format("%02d:00-%02d:59", hour, hour);
    }
}
