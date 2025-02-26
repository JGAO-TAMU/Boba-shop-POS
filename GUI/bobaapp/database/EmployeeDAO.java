package bobaapp.database;

import bobaapp.models.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public static int getAccessLevel(int employeeID) {
        String query = "SELECT accessLevel FROM Employee WHERE employeeID = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeeID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("accessLevel");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if employee not found or error occurs
    }

    public static List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employee;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                employees.add(new Employee(
                    rs.getInt("employeeID"),
                    rs.getString("name"),
                    rs.getInt("accessLevel"),
                    rs.getTimestamp("clockIn"),
                    rs.getTimestamp("clockOut")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public static void addEmployee(Employee employee) {
        String query = "INSERT INTO Employee (employeeID, name, accessLevel, clockIn, clockOut) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employee.getEmployeeID());
            pstmt.setString(2, employee.getName());
            pstmt.setInt(3, employee.getAccessLevel());
            
            // Handle null timestamp values
            if (employee.getClockIn() != null) {
                pstmt.setTimestamp(4, employee.getClockIn());
            } else {
                pstmt.setNull(4, java.sql.Types.TIMESTAMP);
            }
            
            if (employee.getClockOut() != null) {
                pstmt.setTimestamp(5, employee.getClockOut());
            } else {
                pstmt.setNull(5, java.sql.Types.TIMESTAMP);
            }
            
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateEmployee(Employee employee) {
        String query = "UPDATE Employee SET name = ?, accessLevel = ?, clockIn = ?, clockOut = ? WHERE employeeID = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, employee.getName());
            pstmt.setInt(2, employee.getAccessLevel());
            pstmt.setTimestamp(3, employee.getClockIn());
            pstmt.setTimestamp(4, employee.getClockOut());
            pstmt.setInt(5, employee.getEmployeeID());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeEmployee(int employeeID) {
        String query = "DELETE FROM Employee WHERE employeeID = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeeID);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
