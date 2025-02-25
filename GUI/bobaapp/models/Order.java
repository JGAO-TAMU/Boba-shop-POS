package bobaapp.models;

import java.sql.Timestamp;

public class Order {
    private int orderID;
    private Timestamp timestamp;
    private double price;
    private int employeeID;

    public Order(int orderID, Timestamp timestamp, double price, int employeeID) {
        this.orderID = orderID;
        this.timestamp = timestamp;
        this.price = price;
        this.employeeID = employeeID;
    }

    public int getOrderID() { return orderID; }
    public Timestamp getTimestamp() { return timestamp; }
    public double getPrice() { return price; }
    public int getEmployeeID() { return employeeID; }
}
