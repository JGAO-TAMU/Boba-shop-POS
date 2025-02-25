package bobaapp.database;

import bobaapp.models.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdersDAO {
    public static void placeOrder(int employeeID, double price) {
        String query = "INSERT INTO orders (timestamp, price, employeeID) VALUES (NOW(), ?, ?) RETURNING orderID;";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, price);
            stmt.setInt(2, employeeID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("Order placed with ID: " + rs.getInt("orderID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders ORDER BY timestamp DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                orders.add(new Order(
                    rs.getInt("orderID"),
                    rs.getTimestamp("timestamp"),
                    rs.getDouble("price"),
                    rs.getInt("employeeID")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
