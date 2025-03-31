import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    // Database URL - Assumes Booktracker.db is in the project's root folder
    // If you run from the 'src' directory, you might need "../Booktracker.db"
    private static final String DB_URL = "jdbc:sqlite:Booktracker.db";

    public static void main(String[] args) {
        // Add the 'Name' column if it doesn't exist (Requirement 9)
        addNameColumnIfNotExists();

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            printMenu();
            try {
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline left-over

                switch (choice) {
                    case 1:
                        addUser(scanner);
                        break;
                    case 2:
                        getUserReadingHabits(scanner);
                        break;
                    case 3:
                        changeBookTitle(scanner);
                        break;
                    case 4:
                        deleteReadingHabit(scanner);
                        break;
                    case 5:
                        displayMeanUserAge();
                        break;
                    case 6:
                        displayUsersForBook(scanner);
                        break;
                    case 7:
                        displayTotalPagesRead();
                        break;
                    case 8:
                        displayUsersReadingMultipleBooks();
                        break;
                    case 9:
                        System.out.println("Functionality 9 (Add Name column) was executed on startup if needed.");
                        break;
                    case 0:
                        System.out.println("Exiting application. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
                choice = -1; // Reset choice to continue loop
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                // Consider more specific error handling or logging
            }
            System.out.println("\n----------------------------------------\n"); // Separator
        }

        scanner.close();
    }

    // --- Database Connection ---
    private static Connection connect() throws SQLException {
        // Ensure the JDBC driver is loaded (optional for modern JDBC, but good practice)
        try {
             Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
             System.err.println("SQLite JDBC driver not found. Make sure sqlite-jdbc-*.jar is in the classpath.");
             throw new SQLException("JDBC Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL);
    }

    // --- Menu ---
    private static void printMenu() {
        System.out.println("\n======== Booktracker Admin Menu ========");
        System.out.println(" 1. Add User");
        System.out.println(" 2. Show User Habits");
        System.out.println(" 3. Change Book Title");
        System.out.println(" 4. Delete Reading Habit");
        System.out.println(" 5. Show Mean User Age");
        System.out.println(" 6. Show User Count for Book");
        System.out.println(" 7. Show Total Pages Read");
        System.out.println(" 8. Show Users Reading >1 Book");
        System.out.println(" --------------------------------------");
        System.out.println(" 9. (Info) Add 'Name' column (startup)");
        System.out.println(" 0. Exit Application");
        System.out.println("========================================");
        System.out.print("Enter your choice: "); // Prompt is part of the menu block
    }

    // --- Functionality Implementations (Stubs for now) ---

    // Requirement 9: Add 'Name' column to User table
    private static void addNameColumnIfNotExists() {
         // SQL to check if the column exists
        String checkColumnSQL = "PRAGMA table_info(User);";
        // SQL to add the column
        String addColumnSQL = "ALTER TABLE User ADD COLUMN Name TEXT;";

        boolean columnExists = false;
        Connection conn = null;
        ResultSet rs = null;
        Statement checkStmt = null;
        Statement addStmt = null;

        try {
            conn = connect();
            // Check if the table exists first (optional but good practice)
            DatabaseMetaData dbm = conn.getMetaData();
            try (ResultSet tables = dbm.getTables(null, null, "User", null)) {
                 if (!tables.next()) {
                    System.out.println("Warning: 'User' table does not exist. Cannot add 'Name' column yet.");
                    // You might want to create tables here if they don't exist based on schema.sql
                    return; // Exit if table doesn't exist
                 }
            }

            // Check if column 'Name' exists
            checkStmt = conn.createStatement();
            rs = checkStmt.executeQuery(checkColumnSQL);
            while (rs.next()) {
                if ("Name".equalsIgnoreCase(rs.getString("name"))) {
                    columnExists = true;
                    break;
                }
            }

            // Add column if it doesn't exist
            if (!columnExists) {
                System.out.println("Column 'Name' not found in 'User' table. Adding it...");
                addStmt = conn.createStatement();
                addStmt.executeUpdate(addColumnSQL);
                System.out.println("Column 'Name' added successfully to 'User' table.");
            } else {
                 System.out.println("Column 'Name' already exists in 'User' table.");
            }

        } catch (SQLException e) {
            System.err.println("Error during schema check/update for 'Name' column: " + e.getMessage());
             // Don't stop the whole app, maybe the table doesn't exist yet.
        } finally {
             // Close resources in reverse order of creation
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (checkStmt != null) checkStmt.close(); } catch (SQLException e) { /* ignored */ }
             try { if (addStmt != null) addStmt.close(); } catch (SQLException e) { /* ignored */ }
             try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignored */ }
        }
    }


    // Requirement 1: Add a user
    private static void addUser(Scanner scanner) throws SQLException {
        System.out.println("--- Add New User ---");
        int userId;
        int age;
        String gender;
        String name; // Added for requirement 9

        try {
            System.out.print("Enter User ID: ");
            userId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Age: ");
            age = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Gender (man/woman): ");
            gender = scanner.nextLine();

            System.out.print("Enter Name: "); // Get the name
            name = scanner.nextLine();

        } catch (InputMismatchException e) {
            System.out.println("Invalid input type. Please enter numbers for ID and Age.");
            scanner.nextLine(); // Consume the invalid input
            return;
        }

        String sql = "INSERT INTO User(userID, age, gender, Name) VALUES(?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connect();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            pstmt.setString(4, name); // Set the name parameter
            int affectedRows = pstmt.executeUpdate();
             if (affectedRows > 0) {
                 System.out.println("User added successfully!");
             } else {
                 System.out.println("User could not be added.");
             }
        } finally {
            // Close resources
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignored */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    // Requirement 2: Get reading habits for a user
    private static void getUserReadingHabits(Scanner scanner) throws SQLException {
         System.out.println("--- Get User Reading Habits ---");
         System.out.print("Enter User ID: ");
         int userId;
         try {
             userId = scanner.nextInt();
             scanner.nextLine(); // Consume newline
         } catch (InputMismatchException e) {
             System.out.println("Invalid input. Please enter a number for User ID.");
             scanner.nextLine(); // Consume invalid input
             return;
         }

         String sql = "SELECT habitID, book, pagesRead, submissionMoment FROM ReadingHabit WHERE userID = ?";
         Connection conn = null;
         PreparedStatement pstmt = null;
         ResultSet rs = null;
         boolean found = false;

         try {
             conn = connect();
             pstmt = conn.prepareStatement(sql);
             pstmt.setInt(1, userId);
             rs = pstmt.executeQuery();

             System.out.println("\nReading Habits for User ID: " + userId);
             System.out.println("-------------------------------------");
             System.out.printf("%-10s %-30s %-10s %-20s%n", "HabitID", "Book Title", "Pages Read", "Timestamp");
             System.out.println("----------------------------------------------------------------------");

             while (rs.next()) {
                 found = true;
                 System.out.printf("%-10d %-30s %-10d %-20s%n",
                         rs.getInt("habitID"),
                         rs.getString("book"),
                         rs.getInt("pagesRead"),
                         rs.getString("submissionMoment"));
             }
             System.out.println("----------------------------------------------------------------------");


             if (!found) {
                 System.out.println("No reading habits found for this user.");
             }
         } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignored */ }
             try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignored */ }
         }
    }

    // Requirement 3: Change book title
    // Note: This changes the title in ALL ReadingHabit entries where it occurs.
    // A better design would have a separate Books table.
    private static void changeBookTitle(Scanner scanner) throws SQLException {
         System.out.println("--- Change Book Title ---");
         System.out.print("Enter the CURRENT book title: ");
         String oldTitle = scanner.nextLine();
         System.out.print("Enter the NEW book title: ");
         String newTitle = scanner.nextLine();

         if (oldTitle.trim().isEmpty() || newTitle.trim().isEmpty()) {
             System.out.println("Book titles cannot be empty.");
             return;
         }

         String sql = "UPDATE ReadingHabit SET book = ? WHERE book = ?";
         Connection conn = null;
         PreparedStatement pstmt = null;

         try {
             conn = connect();
             // Optional: Check if the old title exists first (more user-friendly)
             // Optional: Disable auto-commit for transaction safety if needed
             pstmt = conn.prepareStatement(sql);
             pstmt.setString(1, newTitle);
             pstmt.setString(2, oldTitle);

             int affectedRows = pstmt.executeUpdate();

             if (affectedRows > 0) {
                 System.out.println("Successfully updated " + affectedRows + " record(s) from '" + oldTitle + "' to '" + newTitle + "'.");
             } else {
                 System.out.println("No records found with the title '" + oldTitle + "'. No changes made.");
             }
         } finally {
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignored */ }
             try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignored */ }
         }
    }

    // Requirement 4: Delete a reading habit record
    private static void deleteReadingHabit(Scanner scanner) throws SQLException {
         System.out.println("--- Delete Reading Habit Record ---");
         System.out.print("Enter the Habit ID to delete: ");
         int habitId;
         try {
             habitId = scanner.nextInt();
             scanner.nextLine(); // Consume newline
         } catch (InputMismatchException e) {
             System.out.println("Invalid input. Please enter a number for Habit ID.");
             scanner.nextLine(); // Consume invalid input
             return;
         }

         String sql = "DELETE FROM ReadingHabit WHERE habitID = ?";
         Connection conn = null;
         PreparedStatement pstmt = null;

         try {
             conn = connect();
             pstmt = conn.prepareStatement(sql);
             pstmt.setInt(1, habitId);

             int affectedRows = pstmt.executeUpdate();

             if (affectedRows > 0) {
                 System.out.println("Successfully deleted record with Habit ID: " + habitId);
             } else {
                 System.out.println("No record found with Habit ID: " + habitId + ". No changes made.");
             }
         } finally {
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignored */ }
             try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignored */ }
         }
    }

    // Requirement 5: Calculate and display mean user age
    private static void displayMeanUserAge() throws SQLException {
         System.out.println("--- Calculate Mean User Age ---");
         String sql = "SELECT AVG(age) AS mean_age FROM User"; // Use SQL AVG() function
         Connection conn = null;
         Statement stmt = null;
         ResultSet rs = null;

         try {
             conn = connect();
             stmt = conn.createStatement();
             rs = stmt.executeQuery(sql);

             if (rs.next()) {
                 double meanAge = rs.getDouble("mean_age");
                 // Check if the result is valid (AVG returns NULL if table is empty)
                 if (rs.wasNull()) {
                      System.out.println("There are no users in the database to calculate the mean age.");
                 } else {
                      System.out.printf("The mean age of all users is: %.2f%n", meanAge);
                 }
             } else {
                  // Should not happen with AVG unless table doesn't exist (handled earlier)
                  System.out.println("Could not retrieve mean age.");
             }
         } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
             try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignored */ }
         }
    }

    // Requirement 6: Count users who read a specific book
    private static void displayUsersForBook(Scanner scanner) throws SQLException {
         System.out.println("--- Count Users for Specific Book ---");
         System.out.print("Enter the book title: ");
         String bookTitle = scanner.nextLine();

         if (bookTitle.trim().isEmpty()) {
             System.out.println("Book title cannot be empty.");
             return;
         }

         // Use COUNT(DISTINCT user) to count each user only once for that book
         String sql = "SELECT COUNT(DISTINCT userID) AS user_count FROM ReadingHabit WHERE book = ?";
         Connection conn = null;
         PreparedStatement pstmt = null;
         ResultSet rs = null;

         try {
             conn = connect();
             pstmt = conn.prepareStatement(sql);
             pstmt.setString(1, bookTitle);
             rs = pstmt.executeQuery();

             if (rs.next()) {
                 int count = rs.getInt("user_count");
                 System.out.println("Total number of distinct users who have read pages from '" + bookTitle + "': " + count);
             } else {
                 System.out.println("Could not retrieve user count for the book.");
             }
         } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignored */ }
             try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignored */ }
         }
    }

    // Requirement 7: Calculate total pages read by all users
    private static void displayTotalPagesRead() throws SQLException {
        System.out.println("--- Calculate Total Pages Read ---");
        String sql = "SELECT SUM(pagesRead) AS total_pages FROM ReadingHabit"; // Use SQL SUM() function
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                 long totalPages = rs.getLong("total_pages"); // Use long in case it's a large number
                 // SUM returns NULL if table is empty, which translates to 0 for getLong/getInt
                 if (rs.wasNull()) {
                      System.out.println("No pages have been recorded as read yet.");
                 } else {
                     System.out.println("The total number of pages read by all users is: " + totalPages);
                 }
            } else {
                System.out.println("Could not retrieve total pages read.");
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    // Requirement 8: Count users who have read more than one book
    private static void displayUsersReadingMultipleBooks() throws SQLException {
        System.out.println("--- Count Users Reading More Than One Book ---");
        // SQL: Group habits by user, count distinct books per user, then count users where book count > 1
        String sql = "SELECT COUNT(userID) AS multi_book_user_count " +
                     "FROM (SELECT userID, COUNT(DISTINCT book) as book_count " +
                           "FROM ReadingHabit " +
                           "GROUP BY userID) " +
                     "WHERE book_count > 1";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int count = rs.getInt("multi_book_user_count");
                System.out.println("Total number of users that have read more than one book: " + count);
            } else {
                System.out.println("Could not retrieve the count of users reading multiple books.");
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignored */ }
        }
    }
}