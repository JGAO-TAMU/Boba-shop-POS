package bobaapp.models;

import java.sql.Timestamp;

public class Order {
    private int orderID;
    private Timestamp timestamp;
    private double price;
    private String employeeName;

    public Order(int orderID, Timestamp timestamp, double price, String employeeName) {
        this.orderID = orderID;
        this.timestamp = timestamp;
        this.price = price;
        this.employeeName = employeeName;
    }

    public int getOrderID() { return orderID; }
    public Timestamp getTimestamp() { return timestamp; }
    public double getPrice() { return price; }
    public String getEmployeeName() { return employeeName; }
}
