package bobaapp.database;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Data Access Object for generating various business reports.
 * This class provides methods to retrieve inventory, revenue, and ingredient usage data
 * from the database for reporting purposes.
 *
 * @author Team 10
 * @version 1.0
 */
public class ReportDAO {

    /**
     * Retrieves a list of inventory items that are running low on stock.
     * This method queries the Inventory table for items with quantity less than 1000 units
     * and returns their names sorted by quantity in ascending order.
     *
     * @return List of item names that are low on stock (quantity < 1000)
     * @throws RuntimeException if a database error occurs while retrieving data
     */
    public static List<String> getLowStockItems() {
        List<String> lowStockItems = new ArrayList<>();
        String query = 
        "SELECT name, quantity FROM Inventory WHERE quantity < 1000 ORDER BY quantity;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                lowStockItems.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lowStockItems;
    }

    /**
     * Retrieves the daily revenue trends from the Orders table.
     * This method calculates the total revenue for each day by summing the price
     * of all orders placed on that day.
     *
     * @return Map with dates as keys and total daily revenue as values
     * @throws RuntimeException if a database error occurs while retrieving data
     */
    public static Map<String, Double> getRevenueTrend() {
        Map<String, Double> revenueTrend = new HashMap<>();
        String query = 
        "SELECT DATE_TRUNC('day', timestamp) as date, SUM(price) as daily_revenue FROM Orders GROUP BY date ORDER BY date;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String date = rs.getString("date");
                double totalRevenue = rs.getDouble("daily_revenue");

                revenueTrend.put(date, totalRevenue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenueTrend;
    }
    /**
     * Returns the total revenue for each day within the specified time frame
     * @param days Number of days to look back
     * @return Map of revenue trend
     * @throws SQLException
     */
    public static Map<String, Double> getRevenueTrendByTimeFrame(int days) {
        Map<String, Double> revenueTrend = new HashMap<>();
        String query = 
            "SELECT DATE_TRUNC('day', timestamp) as date, SUM(price) as daily_revenue " +
            "FROM Orders " +
            "WHERE timestamp >= CURRENT_DATE - INTERVAL '" + days + " days' " +
            "GROUP BY date " +
            "ORDER BY date;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String date = rs.getString("date");
                double totalRevenue = rs.getDouble("daily_revenue");

                revenueTrend.put(date, totalRevenue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenueTrend;
    }

    /**
     * Retrieves the usage statistics for each ingredient.
     * This method calculates the total quantity used for each ingredient across all orders
     * and sorts the results by total usage in descending order.
     *
     * @return Map with ingredient names as keys and their total usage quantities as values
     * @throws RuntimeException if a database error occurs while retrieving data
     */
    public static Map<String, Integer> getIngredientUsage() {
        Map<String, Integer> ingredientUsage = new HashMap<>();
        String query = 
        "SELECT i.name as ingredient, SUM(di.quantityUsed) as total_usage FROM DrinkIngredients di JOIN Inventory i ON di.ingredientID = i.ingredientID GROUP BY i.name ORDER BY total_usage DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String ingredient = rs.getString("ingredient");
                int totalUsage = rs.getInt("total_usage");

                ingredientUsage.put(ingredient, totalUsage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ingredientUsage;
    }

    /**
     * Retrieves sales data by menu item within a specified time frame.
     * This method counts the number of times each menu item appears in orders
     * within the specified number of days from the current date.
     *
     * @param days Number of days to look back from the current date
     * @return Map with menu item names as keys and their sales quantities as values
     * @throws RuntimeException if a database error occurs while retrieving data
     */
    public static Map<String, Integer> getSalesByItem(int days) {
        Map<String, Integer> salesByItem = new HashMap<>();
        String query = 
            "SELECT m.name AS item_name, COUNT(d.drinkId) AS quantity " +
            "FROM Drinks d " +
            "JOIN Menu m ON d.menuId = m.menuId " +
            "JOIN Orders o ON d.orderId = o.orderId " +
            "WHERE o.timestamp >= CURRENT_DATE - INTERVAL '" + days + " days' " +
            "GROUP BY m.name " +
            "ORDER BY quantity DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String itemName = rs.getString("item_name");
                int quantity = rs.getInt("quantity");
                salesByItem.put(itemName, quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salesByItem;
    }
      /**
     * Retrieves the usage statistics for each ingredient within a specified time frame.
     * This method calculates the total quantity used for each ingredient across all orders
     * within the specified number of days from the current date.
     *
     * @param days Number of days to look back from the current date
     * @return Map with ingredient names as keys and their total usage quantities as values
     * @throws RuntimeException if a database error occurs while retrieving data
     */
public static Map<String, Integer> getProductUsage(int days) {
    Map<String, Integer> productUsage = new HashMap<>();
    String query = 
        "SELECT i.name AS ingredient, SUM(di.quantityUsed) AS total_usage " +
        "FROM DrinkIngredients di " +
        "JOIN Inventory i ON di.ingredientID = i.ingredientID " +
        "JOIN Menu m ON di.menuID = m.menuID " +  
        "JOIN Drinks d ON m.menuID = d.menuID " +  
        "JOIN Orders o ON d.orderID = o.orderID " +
        "WHERE o.timestamp >= CURRENT_DATE - (? * INTERVAL '1 day') " + // Fixed INTERVAL
        "GROUP BY i.name " +
        "ORDER BY total_usage DESC;";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setInt(1, days);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String ingredient = rs.getString("ingredient");
            int totalUsage = rs.getInt("total_usage");

            productUsage.put(ingredient, totalUsage);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return productUsage;
}
}