import java.sql.*;

// DAO for User entity operations
public class UserDao {

    public boolean addUser(int userId, int age, String gender, String name) throws SQLException {
        String sql = "INSERT INTO User(userID, age, gender, Name) VALUES(?, ?, ?, ?)";
        boolean success = false;

        // Use try-with-resources connecting via helper class
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            pstmt.setString(4, name);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        }
        // Let SQLException propagate up to be handled by Main
        return success;
    }

    public double getMeanUserAge() throws SQLException {
        String sql = "SELECT AVG(age) AS mean_age FROM User";
        double meanAge = -1.0; // Indicate error or no data initially

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                meanAge = rs.getDouble("mean_age");
                if (rs.wasNull()) {
                    meanAge = -1.0; // Indicate no users found
                }
            }
        }
        return meanAge;
    }

    // Future methods: getUserById, getAllUsers, updateUser, deleteUser...
}