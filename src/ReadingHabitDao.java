import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO for ReadingHabit entity operations
public class ReadingHabitDao {

    public List<ReadingHabit> getHabitsByUserId(int userId) throws SQLException {
        List<ReadingHabit> habits = new ArrayList<>();
        // SQL query joining ReadingHabit with Books to get the title
         String sql = """
                      SELECT rh.habitID, rh.userID, rh.bookID, b.title, rh.pagesRead, rh.submissionMoment
                      FROM ReadingHabit rh
                      JOIN Books b ON rh.bookID = b.bookID
                      WHERE rh.userID = ?
                      ORDER BY rh.submissionMoment DESC
                      """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Create ReadingHabit object using data (including joined title)
                    ReadingHabit habit = new ReadingHabit(
                        rs.getInt("habitID"),
                        rs.getInt("userID"),
                        rs.getInt("bookID"),
                        rs.getInt("pagesRead"),
                        rs.getString("submissionMoment"),
                        rs.getString("title") // Title from Books table
                    );
                    habits.add(habit);
                }
            }
        }
        return habits;
    }

    public boolean deleteHabitById(int habitId) throws SQLException {
        String sql = "DELETE FROM ReadingHabit WHERE habitID = ?";
        boolean success = false;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, habitId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        }
        return success;
    }

    public int countUsersForBookTitle(String bookTitle) throws SQLException {
        String sql = """
                     SELECT COUNT(DISTINCT rh.userID) AS user_count
                     FROM ReadingHabit rh
                     JOIN Books b ON rh.bookID = b.bookID
                     WHERE b.title = ?
                     """;
        int count = 0;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookTitle);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("user_count");
                }
            }
        }
        return count;
    }

    public long getTotalPagesRead() throws SQLException {
        String sql = "SELECT SUM(pagesRead) AS total_pages FROM ReadingHabit";
        long totalPages = 0; // Default to 0

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                 totalPages = rs.getLong("total_pages");
                 // No need to check wasNull, getLong returns 0 if SUM is NULL
            }
        }
        return totalPages;
    }

     public int countUsersReadingMultipleBooks() throws SQLException {
         String sql = """
                      SELECT COUNT(userID) AS multi_book_user_count
                      FROM (SELECT userID, COUNT(DISTINCT bookID) as book_count
                            FROM ReadingHabit
                            GROUP BY userID)
                      WHERE book_count > 1
                      """;
         int count = 0;
         try (Connection conn = DatabaseConnector.connect();
              Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery(sql)) {

             if (rs.next()) {
                 count = rs.getInt("multi_book_user_count");
             }
         }
         return count;
     }

     // Future method: addReadingHabit(ReadingHabit habit) ...
}