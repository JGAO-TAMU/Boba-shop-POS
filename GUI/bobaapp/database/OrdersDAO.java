package bobaapp.database;

import bobaapp.models.Order;
import bobaapp.models.OrderItem;
import bobaapp.models.HourlySales;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class OrdersDAO {
    // Place a new order and return the order ID
    public static int placeOrder(int employeeId, double price) {
        int orderId = -1;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            System.out.println("connected to db and started transaction");
            try {
                // Use the sequence directly - simpler and avoids locking
                try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO orders (timestamp, price, employeeid) VALUES (NOW(), ?, ?) RETURNING orderid")) {
                    
                    pstmt.setDouble(1, price);
                    pstmt.setInt(2, employeeId);
                    System.out.println("attempting to execute query");
                    try (ResultSet rs = pstmt.executeQuery()) {
                        System.out.println("executed query");
                        if (rs.next()) {
                            orderId = rs.getInt(1);
                            System.out.println("Order placed with ID: " + orderId);
                        }
                    }
                }
                
                conn.commit();
            } catch (SQLException ex) {
                System.err.println("Error in transaction, rolling back: " + ex.getMessage());
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true); // Reset auto-commit
            }
        } catch (SQLException e) {
            System.err.println("Error in placeOrder: " + e.getMessage());
            e.printStackTrace();
            orderId = -1;
        }
        
        return orderId;
    }
    
    // Add a drink to an order and return the drink ID
    public static int addDrink(int orderId, int menuId) {
        int drinkId = -1;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            
            try {
                // Fix the drinks sequence before inserting
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(drinkid), 0) + 1 FROM drinks")) {
                    
                    if (rs.next()) {
                        int nextId = rs.getInt(1);
                        updateDrinkSequence(conn, nextId);
                        System.out.println("Updated drinks sequence to " + nextId);
                    }
                }
                
                // Use the sequence safely
                try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO drinks (orderid, menuid) VALUES (?, ?) RETURNING drinkid")) {
                    
                    pstmt.setInt(1, orderId);
                    pstmt.setInt(2, menuId);
                    
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            drinkId = rs.getInt(1);
                            System.out.println("Added drink with ID: " + drinkId);
                        }
                    }
                }
                
                conn.commit();
            } catch (SQLException ex) {
                System.err.println("Error in addDrink transaction, rolling back: " + ex.getMessage());
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error in addDrink: " + e.getMessage());
            e.printStackTrace();
            drinkId = -1;
        }
        
        return drinkId;
    }

    // Update the drinks sequence to a specific value - Fixed to use a string concatenation
    // instead of a parameter placeholder which was causing the SQL error
    private static void updateDrinkSequence(Connection conn, int newValue) {
        try (Statement stmt = conn.createStatement()) {
            String sql = "ALTER SEQUENCE drinks_drinkid_seq RESTART WITH " + newValue;
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error updating drink sequence: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Public method to reset the drinks sequence
    public static boolean resetDrinksSequence() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Find the maximum drink ID
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(drinkid), 0) + 1 FROM drinks")) {
                
                if (rs.next()) {
                    int nextId = rs.getInt(1);
                    updateDrinkSequence(conn, nextId);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error resetting drinks sequence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Add a modification to a drink
    public static boolean addModification(int drinkId, int modMenuId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            
            try {
                // Let PostgreSQL handle the ID generation using the sequence
                try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO modifications (drinkid, modmenuid) VALUES (?, ?)")) {
                    
                    pstmt.setInt(1, drinkId);
                    pstmt.setInt(2, modMenuId);
                    
                    int rowsAffected = pstmt.executeUpdate();
                    conn.commit();
                    return rowsAffected > 0;
                }
            } catch (SQLException ex) {
                System.err.println("Error in addModification transaction, rolling back: " + ex.getMessage());
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true); // Reset auto-commit
            }
        } catch (SQLException e) {
            System.err.println("Error in addModification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get modification menu ID by name
    public static int getModMenuIdByName(String name) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT modmenuid FROM modificationsmenu WHERE name = ?")) {
            
            pstmt.setString(1, name);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("modmenuid");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }

    // Get all orders with employee names
    public static List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT o.orderid, o.timestamp, o.price, e.name AS employee_name " +
                "FROM orders o JOIN employee e ON o.employeeid = e.employeeid " +
                "ORDER BY o.timestamp DESC")) {
            
            while (rs.next()) {
                int orderId = rs.getInt("orderid");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                double price = rs.getDouble("price");
                String employeeName = rs.getString("employee_name");
                
                orders.add(new Order(orderId, timestamp, price, employeeName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }

    // Get today's orders with employee names
    public static List<Order> getTodaysOrders() {
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT o.orderid, o.timestamp, o.price, e.name AS employee_name " +
                "FROM orders o JOIN employee e ON o.employeeid = e.employeeid " +
                "WHERE DATE(o.timestamp) = CURRENT_DATE " +
                "ORDER BY o.timestamp DESC")) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("orderid");
                    Timestamp timestamp = rs.getTimestamp("timestamp");
                    double price = rs.getDouble("price");
                    String employeeName = rs.getString("employee_name");
                    
                    orders.add(new Order(orderId, timestamp, price, employeeName));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }

    // Get orders summary for today
    public static double getTodaysTotalSales() {
        double totalSales = 0.0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT SUM(price) AS total_sales FROM orders " +
                "WHERE DATE(timestamp) = CURRENT_DATE")) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalSales = rs.getDouble("total_sales");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating today's sales: " + e.getMessage());
            e.printStackTrace();
        }
        
        return totalSales;
    }

    // Update the orders sequence to a specific value
    private static void updateOrderSequence(Connection conn, int newValue) {
        try (Statement stmt = conn.createStatement()) {
            String sql = "ALTER SEQUENCE orders_orderid_seq RESTART WITH " + newValue;
            stmt.execute(sql);
            System.out.println("Updated orders sequence to " + newValue);
        } catch (SQLException e) {
            System.err.println("Error updating order sequence: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Public method to reset the orders sequence - Updated without exclusive lock
    public static boolean resetOrdersSequence() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Find the maximum order ID WITHOUT exclusive locks
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(orderid), 0) + 1 FROM orders")) {
                
                if (rs.next()) {
                    int nextId = rs.getInt(1);
                    // Update the sequence directly
                    String sql = "ALTER SEQUENCE orders_orderid_seq RESTART WITH " + nextId;
                    stmt.execute(sql);
                    System.out.println("Orders sequence reset to " + nextId);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error resetting orders sequence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Get sales data by hour for today
    public static List<HourlySales> getTodaySalesByHour() {
        List<HourlySales> hourlyData = new ArrayList<>();
        Map<Integer, HourlySales> hourlyMap = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT EXTRACT(HOUR FROM timestamp) as hour, " +
                "COUNT(*) as order_count, SUM(price) as total_sales " +
                "FROM orders " +
                "WHERE DATE(timestamp) = CURRENT_DATE " +
                "GROUP BY EXTRACT(HOUR FROM timestamp) " +
                "ORDER BY hour")) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int hour = rs.getInt("hour");
                    double totalSales = rs.getDouble("total_sales");
                    int orderCount = rs.getInt("order_count");
                    
                    hourlyMap.put(hour, new HourlySales(hour, totalSales, orderCount));
                }
            }
            
            // Fill in missing hours with zero sales
            for (int hour = 0; hour < 24; hour++) {
                if (hourlyMap.containsKey(hour)) {
                    hourlyData.add(hourlyMap.get(hour));
                } else {
                    hourlyData.add(new HourlySales(hour, 0.0, 0));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting hourly sales data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return hourlyData;
    }
}
