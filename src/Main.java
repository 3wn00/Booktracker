import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.List; // Import List
import java.util.Scanner;

public class Main {

    // DAOs will handle connections now, DB_URL is in DatabaseConnector
    // private static final String DB_URL = "jdbc:sqlite:Booktracker.db";

    // Instantiate DAOs - could also pass them around if needed
    private static UserDao userDao = new UserDao();
    private static BookDao bookDao = new BookDao();
    private static ReadingHabitDao readingHabitDao = new ReadingHabitDao();


    public static void main(String[] args) {
        // Initial schema checks/setup can stay here or move to a dedicated setup class
        addNameColumnIfNotExists();
        createBooksTableIfNotExists();

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            printMenu();
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline left-over

                // Use a separate method to handle the action based on choice
                handleMenuChoice(choice, scanner);

            } catch (InputMismatchException e) {
                System.err.println("ERROR: Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
                choice = -1; // Reset choice to continue loop
            }

             if (choice != 0) {
                  System.out.println("\n//======= End of Action =======\\");
             }
        } // End while loop

        System.out.println("\nExiting application. Goodbye!");
        scanner.close();
    }

    // Method to handle routing based on menu choice
    private static void handleMenuChoice(int choice, Scanner scanner) {
         switch (choice) {
            case 1:
                addUserAction(scanner);
                break;
            case 2:
                getUserReadingHabitsAction(scanner);
                break;
            case 3:
                changeBookTitleAction(scanner);
                break;
            case 4:
                deleteReadingHabitAction(scanner);
                break;
            case 5:
                displayMeanUserAgeAction();
                break;
            case 6:
                displayUsersForBookAction(scanner);
                break;
            case 7:
                displayTotalPagesReadAction();
                break;
            case 8:
                displayUsersReadingMultipleBooksAction();
                break;
            case 9:
                System.out.println("INFO: Functionality 9 (Add Name column) checked/executed on startup.");
                break;
            case 10:
                addReadingHabitAction(scanner);
                break;
            case 0:
                // Handled in main loop exit condition
                break;
            default:
                System.out.println("WARNING: Invalid choice. Please try again.");
        }
    }


    // --- Menu ---
    private static void printMenu() {
        // (Keep printMenu method as it was - no DB logic)
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
        System.out.println(" 10. Add Reading Habit");
        System.out.println(" 0. Exit Application");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");
    }

    // --- Schema Setup Methods (Keep in Main or move to Setup class) ---

    // Requirement 9: Add 'Name' column to User table
    private static void addNameColumnIfNotExists() {
         // Uses DatabaseConnector now
        String checkColumnSQL = "PRAGMA table_info(User);";
        String addColumnSQL = "ALTER TABLE User ADD COLUMN Name TEXT;";
        boolean columnExists = false;

        try (Connection conn = DatabaseConnector.connect(); // Use connector
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkColumnSQL)) {

            // ... (rest of logic is the same, just uses DatabaseConnector.connect()) ...
            while (rs.next()) {
                if ("Name".equalsIgnoreCase(rs.getString("name"))) {
                    columnExists = true;
                    break;
                }
            }
             if (!columnExists) {
                 DatabaseMetaData dbm = conn.getMetaData();
                 try (ResultSet tables = dbm.getTables(null, null, "User", null)) {
                     if (!tables.next()) {
                         System.out.println("INFO: 'User' table does not exist yet. Cannot add 'Name' column now.");
                         return;
                     }
                 }
                System.out.println("INFO: Column 'Name' not found in 'User' table. Adding it...");
                try (Statement addStmt = conn.createStatement()) {
                     addStmt.executeUpdate(addColumnSQL);
                     System.out.println("SUCCESS: Column 'Name' added successfully to 'User' table.");
                }
            } else {
                 // System.out.println("INFO: Column 'Name' already exists in 'User' table.");
            }

        } catch (SQLException e) {
            System.err.println("WARNING: Error during schema check/update for 'Name' column: " + e.getMessage());
        }
    }

    // Helper to create Books table if needed
     private static void createBooksTableIfNotExists() {
        String sql = """
                     CREATE TABLE IF NOT EXISTS Books (
                         bookID INTEGER PRIMARY KEY AUTOINCREMENT,
                         title TEXT NOT NULL UNIQUE
                     );
                     """;
         // Uses DatabaseConnector now
        try (Connection conn = DatabaseConnector.connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
             System.err.println("WARNING: Error checking/creating 'Books' table: " + e.getMessage());
        }
    }

    // --- Action Methods (Calling DAOs) ---

    // Action for Menu Option 1
    private static void addUserAction(Scanner scanner) {
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

            // Call DAO method
            boolean success = userDao.addUser(userId, age, gender, name);

            if (success) {
                System.out.println("SUCCESS: User added successfully!");
            } else {
                // Specific error (like duplicate ID) should be printed by DAO or caught below
                 System.out.println("INFO: User could not be added (see error above if any).");
            }

        } catch (InputMismatchException e) {
            System.err.println("ERROR: Invalid input type. Please enter numbers for ID and Age.");
            scanner.nextLine(); // Consume the invalid input
        } catch (SQLException e) {
             // Catch specific errors if needed, otherwise print general message
              if (e.getErrorCode() == 19 && e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed")) {
                    System.err.println("ERROR: User ID already exists.");
              } else {
                 System.err.println("ERROR during database operation: " + e.getMessage());
              }
        }
        System.out.println("-----------------------");
    }

    // Action for Menu Option 2
    private static void getUserReadingHabitsAction(Scanner scanner) {
         System.out.println("\n--- 2. Show User Reading Habits ---");
         System.out.print("Enter User ID: ");
         int userId;
         try {
             userId = scanner.nextInt();
             scanner.nextLine(); // Consume newline

             // Call DAO method
             List<ReadingHabit> habits = readingHabitDao.getHabitsByUserId(userId);

             // Display results
             System.out.println("\n>>> Reading Habits for User ID: " + userId);
             System.out.println("----------------------------------------------------------------------");
             System.out.printf("%-10s %-50s %-10s %-20s%n", "HabitID", "Book Title", "Pages Read", "Timestamp");
             System.out.println("----------------------------------------------------------------------");

             if (habits.isEmpty()) {
                 System.out.println("INFO: No reading habits found for this user.");
             } else {
                 for (ReadingHabit habit : habits) {
                      System.out.printf("%-10d %-50s %-10d %-20s%n",
                             habit.getHabitID(),
                             habit.getBookTitle(), // Use getter from ReadingHabit object
                             habit.getPagesRead(),
                             habit.getSubmissionMoment());
                 }
             }
             System.out.println("----------------------------------------------------------------------");

         } catch (InputMismatchException e) {
             System.err.println("ERROR: Invalid input. Please enter a number for User ID.");
             scanner.nextLine(); // Consume invalid input
         } catch (SQLException e) {
             System.err.println("ERROR retrieving habits: " + e.getMessage());
         }
         System.out.println("-----------------------------------");
    }

    // Action for Menu Option 3
    private static void changeBookTitleAction(Scanner scanner) {
        System.out.println("\n--- 3. Change Book Title ---");
        System.out.print("Enter the CURRENT book title: ");
        String oldTitle = scanner.nextLine();
        System.out.print("Enter the NEW book title: ");
        String newTitle = scanner.nextLine();

        if (oldTitle.trim().isEmpty() || newTitle.trim().isEmpty()) {
            System.out.println("WARNING: Book titles cannot be empty.");
            System.out.println("--------------------------");
            return;
        }
         if (oldTitle.trim().equalsIgnoreCase(newTitle.trim())) {
              System.out.println("INFO: New title is the same as the current title. No change needed.");
              System.out.println("--------------------------");
              return;
         }

        try {
            // Call DAO method
            boolean success = bookDao.updateBookTitle(oldTitle, newTitle);

             if (success) {
                 System.out.println("SUCCESS: Updated book title in Books table from '" + oldTitle + "' to '" + newTitle + "'.");
                 System.out.println("INFO: All reading habits associated with this book are automatically updated due to normalization.");
             } else {
                 System.out.println("INFO: No book found with the title '" + oldTitle + "' in the Books table. No changes made.");
             }
        } catch (SQLException e) {
             if (e.getErrorCode() == 19 && e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed")) {
                   System.err.println("ERROR: Cannot change title. A book with the title '" + newTitle + "' already exists.");
             } else {
                   System.err.println("ERROR changing book title: " + e.getMessage());
             }
        }
        System.out.println("--------------------------");
    }

     // Action for Menu Option 4
    private static void deleteReadingHabitAction(Scanner scanner) {
         System.out.println("\n--- 4. Delete Reading Habit Record ---");
         System.out.print("Enter the Habit ID to delete: ");
         int habitId;
         try {
             habitId = scanner.nextInt();
             scanner.nextLine(); // Consume newline

             // Call DAO method
             boolean success = readingHabitDao.deleteHabitById(habitId);

             if (success) {
                 System.out.println("SUCCESS: Deleted record with Habit ID: " + habitId);
             } else {
                 System.out.println("INFO: No record found with Habit ID: " + habitId + ". No changes made.");
             }

         } catch (InputMismatchException e) {
             System.err.println("ERROR: Invalid input. Please enter a number for Habit ID.");
             scanner.nextLine(); // Consume invalid input
         } catch (SQLException e) {
              System.err.println("ERROR deleting habit: " + e.getMessage());
         }
          System.out.println("------------------------------------");
    }

    // Action for Menu Option 5
    private static void displayMeanUserAgeAction() {
        System.out.println("\n--- 5. Show Mean User Age ---");
        try {
            // Call DAO method
            double meanAge = userDao.getMeanUserAge();

            if (meanAge < 0) { // Check for our indicator that no users were found
                 System.out.println("INFO: There are no users in the database to calculate the mean age.");
            } else {
                 System.out.printf("RESULT: The mean age of all users is: %.2f%n", meanAge);
            }
        } catch (SQLException e) {
             System.err.println("ERROR calculating mean age: " + e.getMessage());
        }
         System.out.println("---------------------------");
    }

     // Action for Menu Option 6
    private static void displayUsersForBookAction(Scanner scanner) {
        System.out.println("\n--- 6. Show User Count for Specific Book ---");
        System.out.print("Enter the book title: ");
        String bookTitle = scanner.nextLine();

        if (bookTitle.trim().isEmpty()) {
            System.out.println("WARNING: Book title cannot be empty.");
             System.out.println("------------------------------------------");
            return;
        }

        try {
             // Call DAO method
            int count = readingHabitDao.countUsersForBookTitle(bookTitle);
            System.out.println("RESULT: Total number of distinct users who have read pages from '" + bookTitle + "': " + count);

        } catch (SQLException e) {
             System.err.println("ERROR counting users for book: " + e.getMessage());
        }
         System.out.println("------------------------------------------");
    }

     // Action for Menu Option 7
    private static void displayTotalPagesReadAction() {
        System.out.println("\n--- 7. Show Total Pages Read ---");
        try {
            // Call DAO method
            long totalPages = readingHabitDao.getTotalPagesRead();

             if (totalPages == 0) {
                  // Could be no habits, or pagesRead just sums to 0.
                  // Check if ReadingHabit table is actually empty for a more precise message? (Optional)
                  System.out.println("INFO: Total pages read is 0 (or no habits recorded).");
             } else {
                 System.out.println("RESULT: The total number of pages read by all users is: " + totalPages);
             }
        } catch (SQLException e) {
             System.err.println("ERROR calculating total pages read: " + e.getMessage());
        }
         System.out.println("------------------------------");
    }

    // Action for Menu Option 8
    private static void displayUsersReadingMultipleBooksAction() {
        System.out.println("\n--- 8. Show Users Reading More Than One Book ---");
        try {
            // Call DAO method
            int count = readingHabitDao.countUsersReadingMultipleBooks();
            System.out.println("RESULT: Total number of users that have read more than one distinct book: " + count);

        } catch (SQLException e) {
            System.err.println("ERROR counting users reading multiple books: " + e.getMessage());
        }
         System.out.println("----------------------------------------------");
    }
        // Action for Menu Option 10
    private static void addReadingHabitAction(Scanner scanner) {
        System.out.println("\n--- 10. Add Reading Habit ---");
        int userId;
        String bookTitle;
        int pagesRead;
        int bookId = -1; // Initialize bookId to invalid state

        try {
            System.out.print("Enter User ID for the habit: ");
            userId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Optional: Check if user exists using UserDao first (Good Validation)
            // if (!userDao.userExists(userId)) { // Assuming userExists method exists
            //     System.err.println("ERROR: User with ID " + userId + " does not exist.");
            //     System.out.println("---------------------------");
            //     return;
            // }

            System.out.print("Enter Book Title: ");
            bookTitle = scanner.nextLine();
            if (bookTitle.trim().isEmpty()) {
                System.out.println("WARNING: Book title cannot be empty.");
                System.out.println("---------------------------");
                return;
            }

            System.out.print("Enter Pages Read: ");
            pagesRead = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (pagesRead < 0) {
                System.out.println("WARNING: Pages read cannot be negative.");
                System.out.println("---------------------------");
                return;
            }

            // --- Interaction with DAOs ---
            try {
                // 1. Find or Create Book to get bookID
                // Assuming findOrCreateBook method exists in BookDao as shown previously
                bookId = bookDao.findOrCreateBook(bookTitle);

                // 2. If bookID is valid, add the habit
                if (bookId > 0) {
                    boolean success = readingHabitDao.addHabit(userId, bookId, pagesRead);
                    if (success) {
                        System.out.println("SUCCESS: Reading habit added successfully!");
                    } else {
                        System.out.println("INFO: Could not add reading habit.");
                    }
                } else {
                    // This case should ideally be handled within findOrCreateBook
                    System.err.println("ERROR: Could not find or create book ID for the title.");
                }

            } catch (SQLException e) {
                System.err.println("ERROR during database operation: " + e.getMessage());
            }
            // --- End Interaction with DAOs ---

        } catch (InputMismatchException e) {
            System.err.println("ERROR: Invalid input type. Please enter numbers for ID and Pages Read.");
            scanner.nextLine(); // Consume the invalid input
        }
        System.out.println("---------------------------");
    }
}