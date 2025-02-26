package bobaapp.database;

import bobaapp.models.Order;
import bobaapp.models.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdersDAO {
    private static int getNextOrderId() {
        String query = "SELECT COUNT(*) as count FROM orders;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count") + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error getting order count: " + e.getMessage());
            e.printStackTrace();
        }
        return 1; // Default to 1 if there's an error
    }

    private static int getNextDrinkId() {
        String query = "SELECT COUNT(*) as count FROM drinks;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count") + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error getting drink count: " + e.getMessage());
            e.printStackTrace();
        }
        return 1;
    }

    private static int getNextModificationId() {
        String query = "SELECT COUNT(*) as count FROM modifications;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count") + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error getting modification count: " + e.getMessage());
            e.printStackTrace();
        }
        return 1;
    }

    public static int placeOrder(int employeeID, double price) {
        int orderId = getNextOrderId();
        String query = "INSERT INTO orders (orderid, timestamp, price, employeeid) VALUES (?, NOW(), ?, ?);";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, orderId);
            stmt.setDouble(2, price);
            stmt.setInt(3, employeeID);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Order placed with ID: " + orderId);
                return orderId;
            }
        } catch (SQLException e) {
            System.err.println("Error placing order: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public static int addDrink(int orderId, int menuId) {
        int drinkId = getNextDrinkId();
        String query = "INSERT INTO drinks (drinkid, orderid, menuid) VALUES (?, ?, ?);";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, drinkId);
            stmt.setInt(2, orderId);
            stmt.setInt(3, menuId);
            
            if (stmt.executeUpdate() > 0) {
                return drinkId;
            }
        } catch (SQLException e) {
            System.err.println("Error adding drink: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean addModification(int drinkId, int modMenuId) {
        int modid = getNextModificationId();
        String query = "INSERT INTO modifications (modid, drinkid, modmenuid) VALUES (?, ?, ?);";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, modid);
            stmt.setInt(2, drinkId);
            stmt.setInt(3, modMenuId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding modification: " + e.getMessage());
            e.printStackTrace();
            return false;
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
