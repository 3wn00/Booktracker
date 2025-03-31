## MVP (Minimum Viable Product)

This project successfully implements the core requirements outlined in the PI 8 Assignment 2 specification. The MVP includes:

1.  A command-line Java application using JDBC to connect to an SQLite database (`Booktracker.db`).
2.  Database schema based on the provided ERD, including `User` and `ReadingHabit` tables.
3.  Population of the `User` table via `seed.sql` and the `ReadingHabit` table via CSV import.
4.  Implementation of all required functionalities using SQL for calculations where appropriate:
    * [1] Add a new user.
    * [2] Display all reading habits for a specific user.
    * [3] Update the title of a book across all relevant reading habit records.
    * [4] Delete a specific reading habit record by its ID.
    * [5] Calculate and display the mean age of all users.
    * [6] Count and display the total number of distinct users who have read a specific book.
    * [7] Calculate and display the total number of pages read across all users and habits.
    * [8] Count and display the total number of users who have read more than one distinct book.
    * [9] Dynamically add the `Name` column (TEXT) to the `User` table on application startup if it doesn't exist.

## Potential Future Enhancements / Extra Features

While the current version meets the assignment requirements, the following features could be implemented to further enhance the application (ideas for potential extra credit or future development):

**Database Improvements:**

* **Normalization:** Create a separate `Books` table (`bookID` PK, `title`, `author`, etc.) and modify `ReadingHabit` to use `bookID` as a foreign key. This avoids data redundancy and makes book title updates (Function 3) more efficient and reliable.
* **Data Constraints:** Add more specific SQL constraints (e.g., `CHECK(age > 0)`, `UNIQUE` constraints where applicable).

**Enhanced Functionality:**

* **Add Reading Habit:** Implement a menu option to manually add a new reading habit record directly through the application interface.
* **List All Users/Books:** Add options to display all users or all books (if a `Books` table is added).
* **Advanced Statistics:** Implement more complex SQL queries for insights like:
    * Most read book (by pages or number of readers).
    * Average pages read per user session.
    * Users with no reading habits recorded.
* **Search Functionality:** Allow searching for users by name or books by title/author.
* **Data Validation:** Add more robust input validation in Java (e.g., check if a `userID` exists before adding a habit for them, prevent non-sensical `pagesRead` values).

**Code Quality & Structure:**

* **DAO Pattern:** Refactor the database logic into separate Data Access Object (DAO) classes for `User` and `ReadingHabit` to improve code organization and separation of concerns.
* **Transaction Management:** Implement explicit transaction control (`setAutoCommit(false)`, `commit()`, `rollback()`) for operations involving multiple database writes.
* **Unit Testing:** Introduce JUnit tests for database operations (potentially using an in-memory SQLite database).

**User Interface (CLI):**

* **Improved Output Formatting:** Implement pagination for long lists of results.
* **Screen Clearing:** Attempt OS-specific screen clearing for a cleaner look between menu displays (noting portability issues).