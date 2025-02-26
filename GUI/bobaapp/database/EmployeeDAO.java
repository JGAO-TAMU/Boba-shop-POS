package bobaapp.database;

import java.sql.*;

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
}