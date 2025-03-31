import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    // Database URL - Assumes Booktracker.db is in the project's root folder
    private static final String DB_URL = "jdbc:sqlite:Booktracker.db";

    public static void main(String[] args) {
        // Add the 'Name' column if it doesn't exist (Requirement 9)
        addNameColumnIfNotExists();

        // Add Books table if it doesn't exist (Good practice after normalization)
        createBooksTableIfNotExists();

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            printMenu();
            // Removed the general try-catch for SQLException here to handle it within methods
            try {
                // System.out.print("Enter your choice: "); // Moved prompt into printMenu
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
                        System.out.println("INFO: Functionality 9 (Add Name column) checked/executed on startup.");
                        break;
                    case 0:
                        System.out.println("\nExiting application. Goodbye!");
                        break;
                    default:
                        System.out.println("WARNING: Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.err.println("ERROR: Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
                choice = -1; // Reset choice to continue loop
            }
            // Separator is now printed before the menu in the loop start
            // System.out.println("\n----------------------------------------\n");
             if (choice != 0) {
                  System.out.println("\n//======= End of Action =======\\");
             }
        }

        scanner.close();
    }

    // --- Database Connection ---
    private static Connection connect() throws SQLException {
        // Ensure the JDBC driver is loaded
        try {
             Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
             System.err.println("FATAL ERROR: SQLite JDBC driver not found. Make sure sqlite-jdbc-*.jar is in the classpath.");
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
        System.out.print("Enter your choice: ");
    }

    // --- Functionality Implementations ---

    // Requirement 9: Add 'Name' column to User table
    private static void addNameColumnIfNotExists() {
        String checkColumnSQL = "PRAGMA table_info(User);";
        String addColumnSQL = "ALTER TABLE User ADD COLUMN Name TEXT;";
        boolean columnExists = false;

        // Use try-with-resources for automatic closing of resources
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkColumnSQL)) {

            while (rs.next()) {
                if ("Name".equalsIgnoreCase(rs.getString("name"))) {
                    columnExists = true;
                    break;
                }
            }

            if (!columnExists) {
                 // Check if table exists before altering (optional but safer)
                 DatabaseMetaData dbm = conn.getMetaData();
                 try (ResultSet tables = dbm.getTables(null, null, "User", null)) {
                     if (!tables.next()) {
                         System.out.println("INFO: 'User' table does not exist yet. Cannot add 'Name' column now.");
                         return;
                     }
                 }

                System.out.println("INFO: Column 'Name' not found in 'User' table. Adding it...");
                // Use a separate statement for the update
                try (Statement addStmt = conn.createStatement()) {
                     addStmt.executeUpdate(addColumnSQL);
                     System.out.println("SUCCESS: Column 'Name' added successfully to 'User' table.");
                }
            } else {
                 // This message is normal on subsequent runs
                 // System.out.println("INFO: Column 'Name' already exists in 'User' table.");
            }

        } catch (SQLException e) {
            // Log error but allow app to continue if possible
            System.err.println("WARNING: Error during schema check/update for 'Name' column: " + e.getMessage());
        }
    }

    // Helper to create Books table if needed (useful after normalization)
     private static void createBooksTableIfNotExists() {
        String sql = """
                     CREATE TABLE IF NOT EXISTS Books (
                         bookID INTEGER PRIMARY KEY AUTOINCREMENT,
                         title TEXT NOT NULL UNIQUE
                     );
                     """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
             System.err.println("WARNING: Error checking/creating 'Books' table: " + e.getMessage());
        }
    }

    // Requirement 1: Add a user
    private static void addUser(Scanner scanner) {
        System.out.println("\n--- 1. Add New User ---");
        int userId;
        int age;
        String gender;
        String name;

        try {
            System.out.print("Enter User ID: ");
            userId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Age: ");
            age = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Gender: ");
            gender = scanner.nextLine();

            System.out.print("Enter Name: ");
            name = scanner.nextLine();

            String sql = "INSERT INTO User(userID, age, gender, Name) VALUES(?, ?, ?, ?)";

            // Use try-with-resources for Connection and PreparedStatement
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, userId);
                pstmt.setInt(2, age);
                pstmt.setString(3, gender);
                pstmt.setString(4, name);
                int affectedRows = pstmt.executeUpdate();

                 if (affectedRows > 0) {
                     System.out.println("SUCCESS: User added successfully!");
                 } else {
                     System.out.println("INFO: User could not be added (maybe ID exists or other issue).");
                 }
            } catch (SQLException e) {
                // Provide more specific error feedback if possible (e.g., UNIQUE constraint)
                if (e.getErrorCode() == 19 && e.getMessage().contains("UNIQUE constraint failed")) { // SQLite UNIQUE constraint error code
                     System.err.println("ERROR: User ID " + userId + " already exists.");
                } else {
                     System.err.println("ERROR adding user: " + e.getMessage());
                }
            }

        } catch (InputMismatchException e) {
            System.err.println("ERROR: Invalid input type. Please enter numbers for ID and Age.");
            scanner.nextLine(); // Consume the invalid input
        }
        System.out.println("-----------------------"); // Footer
    }

    // Requirement 2: Get reading habits for a user (MODIFIED FOR NORMALIZATION)
    private static void getUserReadingHabits(Scanner scanner) {
         System.out.println("\n--- 2. Show User Reading Habits ---");
         System.out.print("Enter User ID: ");
         int userId;
         try {
             userId = scanner.nextInt();
             scanner.nextLine(); // Consume newline

             // SQL query joining ReadingHabit with Books to get the title
             String sql = """
                          SELECT rh.habitID, b.title, rh.pagesRead, rh.submissionMoment
                          FROM ReadingHabit rh
                          JOIN Books b ON rh.bookID = b.bookID
                          WHERE rh.userID = ?
                          ORDER BY rh.submissionMoment DESC
                          """; // Added ORDER BY

             boolean found = false;

             // Use try-with-resources for auto-closing
             try (Connection conn = connect();
                  PreparedStatement pstmt = conn.prepareStatement(sql)) {

                 pstmt.setInt(1, userId);
                 try (ResultSet rs = pstmt.executeQuery()) { // Also use try-with-resources for ResultSet

                     System.out.println("\n>>> Reading Habits for User ID: " + userId);
                     System.out.println("----------------------------------------------------------------------");
                     // Adjusted printf formatting slightly
                     System.out.printf("%-10s %-50s %-10s %-20s%n", "HabitID", "Book Title", "Pages Read", "Timestamp");
                     System.out.println("----------------------------------------------------------------------");

                     while (rs.next()) {
                         found = true;
                         System.out.printf("%-10d %-50s %-10d %-20s%n",
                                 rs.getInt("habitID"),
                                 rs.getString("title"), // Get title from Books table
                                 rs.getInt("pagesRead"),
                                 rs.getString("submissionMoment"));
                     }
                     System.out.println("----------------------------------------------------------------------");

                     if (!found) {
                         System.out.println("INFO: No reading habits found for this user.");
                     }
                 } // ResultSet closed here
             } catch (SQLException e) {
                 System.err.println("ERROR retrieving habits: " + e.getMessage());
             } // Connection and PreparedStatement closed here

         } catch (InputMismatchException e) {
             System.err.println("ERROR: Invalid input. Please enter a number for User ID.");
             scanner.nextLine(); // Consume invalid input
         }
         System.out.println("-----------------------------------"); // Footer
    }

    // Requirement 3: Change book title (MODIFIED FOR NORMALIZATION)
    private static void changeBookTitle(Scanner scanner) {
         System.out.println("\n--- 3. Change Book Title ---");
         System.out.print("Enter the CURRENT book title: ");
         String oldTitle = scanner.nextLine();
         System.out.print("Enter the NEW book title: ");
         String newTitle = scanner.nextLine();

         if (oldTitle.trim().isEmpty() || newTitle.trim().isEmpty()) {
             System.out.println("WARNING: Book titles cannot be empty.");
             return;
         }
         if (oldTitle.trim().equalsIgnoreCase(newTitle.trim())) {
              System.out.println("INFO: New title is the same as the current title. No change needed.");
              return;
         }

         // This query now updates the Books table directly
         String sql = "UPDATE Books SET title = ? WHERE title = ?";

         // Use try-with-resources
         try (Connection conn = connect();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {

             pstmt.setString(1, newTitle);
             pstmt.setString(2, oldTitle);

             int affectedRows = pstmt.executeUpdate();

             if (affectedRows > 0) {
                 System.out.println("SUCCESS: Updated book title in Books table from '" + oldTitle + "' to '" + newTitle + "'.");
                 System.out.println("INFO: All reading habits associated with this book are automatically updated due to normalization.");
             } else {
                 System.out.println("INFO: No book found with the title '" + oldTitle + "' in the Books table. No changes made.");
             }
         } catch (SQLException e) {
             // Check for unique constraint violation on the NEW title
              if (e.getErrorCode() == 19 && e.getMessage().contains("UNIQUE constraint failed")) {
                    System.err.println("ERROR: Cannot change title. A book with the title '" + newTitle + "' already exists.");
              } else {
                    System.err.println("ERROR changing book title: " + e.getMessage());
              }
         }
         System.out.println("--------------------------"); // Footer
    }

    // Requirement 4: Delete a reading habit record (No changes needed for normalization itself)
    private static void deleteReadingHabit(Scanner scanner) {
         System.out.println("\n--- 4. Delete Reading Habit Record ---");
         System.out.print("Enter the Habit ID to delete: ");
         int habitId;
         try {
             habitId = scanner.nextInt();
             scanner.nextLine(); // Consume newline

             String sql = "DELETE FROM ReadingHabit WHERE habitID = ?";

             // Use try-with-resources
             try (Connection conn = connect();
                  PreparedStatement pstmt = conn.prepareStatement(sql)) {

                 pstmt.setInt(1, habitId);
                 int affectedRows = pstmt.executeUpdate();

                 if (affectedRows > 0) {
                     System.out.println("SUCCESS: Deleted record with Habit ID: " + habitId);
                 } else {
                     System.out.println("INFO: No record found with Habit ID: " + habitId + ". No changes made.");
                 }
             } catch (SQLException e) {
                  System.err.println("ERROR deleting habit: " + e.getMessage());
             }

         } catch (InputMismatchException e) {
             System.err.println("ERROR: Invalid input. Please enter a number for Habit ID.");
             scanner.nextLine(); // Consume invalid input
         }
          System.out.println("------------------------------------"); // Footer
    }

    // Requirement 5: Calculate and display mean user age (No changes needed)
    private static void displayMeanUserAge() {
         System.out.println("\n--- 5. Show Mean User Age ---");
         String sql = "SELECT AVG(age) AS mean_age FROM User";

         // Use try-with-resources
         try (Connection conn = connect();
              Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery(sql)) {

             if (rs.next()) {
                 double meanAge = rs.getDouble("mean_age");
                 if (rs.wasNull()) { // Check if AVG returned NULL (e.g., no users)
                      System.out.println("INFO: There are no users in the database to calculate the mean age.");
                 } else {
                      System.out.printf("RESULT: The mean age of all users is: %.2f%n", meanAge);
                 }
             } else {
                  // This case is unlikely with AVG but included for completeness
                  System.out.println("INFO: Could not retrieve mean age.");
             }
         } catch (SQLException e) {
              System.err.println("ERROR calculating mean age: " + e.getMessage());
         }
          System.out.println("---------------------------"); // Footer
    }

    // Requirement 6: Count users who read a specific book (MODIFIED FOR NORMALIZATION)
    private static void displayUsersForBook(Scanner scanner) {
         System.out.println("\n--- 6. Show User Count for Specific Book ---");
         System.out.print("Enter the book title: ");
         String bookTitle = scanner.nextLine();

         if (bookTitle.trim().isEmpty()) {
             System.out.println("WARNING: Book title cannot be empty.");
             return;
         }

         // Query joins ReadingHabit and Books, filters by title, counts distinct users
         String sql = """
                      SELECT COUNT(DISTINCT rh.userID) AS user_count
                      FROM ReadingHabit rh
                      JOIN Books b ON rh.bookID = b.bookID
                      WHERE b.title = ?
                      """;

         // Use try-with-resources
         try (Connection conn = connect();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {

             pstmt.setString(1, bookTitle);
             try (ResultSet rs = pstmt.executeQuery()) { // try-with-resources for ResultSet

                 if (rs.next()) {
                     int count = rs.getInt("user_count");
                     System.out.println("RESULT: Total number of distinct users who have read pages from '" + bookTitle + "': " + count);
                 } else {
                      // COUNT should always return a row, even if count is 0
                     System.out.println("INFO: Could not retrieve user count (unexpected error).");
                 }
             } // ResultSet closed here

         } catch (SQLException e) {
              System.err.println("ERROR counting users for book: " + e.getMessage());
         }
          System.out.println("------------------------------------------"); // Footer
    }

    // Requirement 7: Calculate total pages read by all users (No changes needed)
    private static void displayTotalPagesRead() {
        System.out.println("\n--- 7. Show Total Pages Read ---");
        String sql = "SELECT SUM(pagesRead) AS total_pages FROM ReadingHabit";

        // Use try-with-resources
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                 long totalPages = rs.getLong("total_pages");
                 if (rs.wasNull()) { // SUM returns NULL if table is empty, getLong interprets as 0
                      System.out.println("INFO: No pages have been recorded as read yet.");
                 } else {
                     System.out.println("RESULT: The total number of pages read by all users is: " + totalPages);
                 }
            } else {
                 System.out.println("INFO: Could not retrieve total pages read.");
            }
        } catch (SQLException e) {
              System.err.println("ERROR calculating total pages read: " + e.getMessage());
        }
         System.out.println("------------------------------"); // Footer
    }

    // Requirement 8: Count users who have read more than one book (MODIFIED FOR NORMALIZATION)
    private static void displayUsersReadingMultipleBooks() {
        System.out.println("\n--- 8. Show Users Reading More Than One Book ---");
        // Modified to count distinct bookID instead of book title
        String sql = """
                     SELECT COUNT(userID) AS multi_book_user_count
                     FROM (SELECT userID, COUNT(DISTINCT bookID) as book_count
                           FROM ReadingHabit
                           GROUP BY userID)
                     WHERE book_count > 1
                     """;

        // Use try-with-resources
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt("multi_book_user_count");
                System.out.println("RESULT: Total number of users that have read more than one distinct book: " + count);
            } else {
                 // COUNT should always return a row
                System.out.println("INFO: Could not retrieve the count of users reading multiple books.");
            }
        } catch (SQLException e) {
             System.err.println("ERROR counting users reading multiple books: " + e.getMessage());
        }
         System.out.println("----------------------------------------------"); // Footer
    }
}