import java.sql.*;

// DAO for Book entity operations
public class BookDao {

    public boolean updateBookTitle(String oldTitle, String newTitle) throws SQLException {
        String sql = "UPDATE Books SET title = ? WHERE title = ?";
        boolean success = false;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newTitle);
            pstmt.setString(2, oldTitle);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        }
        // Let SQLException (like unique constraint violation) propagate up
        return success;
    }

     // Helper method might be needed if implementing "Add Habit" later
     public int findOrCreateBook(String title) throws SQLException {
        String findSql = "SELECT bookID FROM Books WHERE title = ?";
        String insertSql = "INSERT INTO Books (title) VALUES (?)";
        int bookId = -1;

        try (Connection conn = DatabaseConnector.connect()) {
            // Try finding first
            try (PreparedStatement findPstmt = conn.prepareStatement(findSql)) {
                findPstmt.setString(1, title);
                try (ResultSet rs = findPstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("bookID"); // Found existing book
                    }
                }
            }

            // If not found, insert it
            try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                 insertPstmt.setString(1, title);
                 int affectedRows = insertPstmt.executeUpdate();
                 if (affectedRows > 0) {
                     try (ResultSet generatedKeys = insertPstmt.getGeneratedKeys()) {
                         if (generatedKeys.next()) {
                             return generatedKeys.getInt(1); // Return newly generated bookID
                         }
                     }
                 }
            }
        }
         // Should ideally throw specific error if book couldn't be found or created
        if (bookId == -1) throw new SQLException("Could not find or create book with title: " + title);
         return bookId;
     }

    // Future methods: addBook, getBookById, getAllBooks, deleteBook...
}