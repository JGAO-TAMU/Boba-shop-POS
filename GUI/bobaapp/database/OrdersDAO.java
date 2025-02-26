package bobaapp.database;

import bobaapp.models.Order;
import bobaapp.models.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdersDAO {
    // Place a new order and return the order ID
    public static int placeOrder(int employeeId, double price) {
        int orderId = -1;
        
        try (Connection conn = DatabaseConnection.getConnection();
             // Make sure we don't specify any columns - let the DB handle it all
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO orders (timestamp, price, employeeid) VALUES (NOW(), ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setDouble(1, price);
            pstmt.setInt(2, employeeId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in placeOrder: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orderId;
    }
    
    // Add a drink to an order and return the drink ID
    public static int addDrink(int orderId, int menuId) {
        int drinkId = -1;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // First find the maximum drinkid and add 1 to ensure uniqueness
            try (Statement seqStmt = conn.createStatement();
                 ResultSet seqRs = seqStmt.executeQuery("SELECT COALESCE(MAX(drinkid), 0) + 1 FROM drinks")) {
                
                if (seqRs.next()) {
                    drinkId = seqRs.getInt(1);
                    
                    // Now insert using the manually generated ID
                    try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO drinks (drinkid, orderid, menuid) VALUES (?, ?, ?)")) {
                        
                        pstmt.setInt(1, drinkId);
                        pstmt.setInt(2, orderId);
                        pstmt.setInt(3, menuId);
                        
                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected <= 0) {
                            drinkId = -1;  // Reset if insert failed
                        }
                    }
                }
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
            // Find the next modification ID
            int modId;
            try (Statement seqStmt = conn.createStatement();
                 ResultSet seqRs = seqStmt.executeQuery("SELECT COALESCE(MAX(modid), 0) + 1 FROM modifications")) {
                
                if (seqRs.next()) {
                    modId = seqRs.getInt(1);
                    
                    // Use the manually generated ID
                    try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO modifications (modid, drinkid, modmenuid) VALUES (?, ?, ?)")) {
                        
                        pstmt.setInt(1, modId);
                        pstmt.setInt(2, drinkId);
                        pstmt.setInt(3, modMenuId);
                        
                        int rowsAffected = pstmt.executeUpdate();
                        
                        // Also update the sequence to prevent future issues
                        if (rowsAffected > 0) {
                            try (Statement updateStmt = conn.createStatement()) {
                                updateStmt.execute("ALTER SEQUENCE modifications_modid_seq RESTART WITH " + (modId + 1));
                            }
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in addModification: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
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
}
