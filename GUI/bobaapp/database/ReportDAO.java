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
 * @author Claire Wang
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
}