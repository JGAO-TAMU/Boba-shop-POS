package bobaapp.database;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ReportDAO {

    // public static Map<String, Map<String, Integer>> getOrdersOverTime() {
    //     Map<String, Map<String, Integer>> ordersOverTime = new HashMap<>();
    //     String query = "SELECT menuItem, date, COUNT(*) as orderCount FROM Orders GROUP BY menuItem, date ORDER BY date;";

    //     try (Connection conn = DatabaseConnection.getConnection();
    //          Statement stmt = conn.createStatement();
    //          ResultSet rs = stmt.executeQuery(query)) {

    //         while (rs.next()) {
    //             String menuItem = rs.getString("menuItem");
    //             String date = rs.getString("date");
    //             int orderCount = rs.getInt("orderCount");

    //             ordersOverTime.putIfAbsent(menuItem, new HashMap<>());
    //             ordersOverTime.get(menuItem).put(date, orderCount);
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }

    //     return ordersOverTime;
    // }

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