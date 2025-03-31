## MVP (Minimum Viable Product)

This project successfully implements the core requirements outlined in the PI 8 Assignment 2 specification. The MVP includes:

1.  A command-line Java application using JDBC to connect to an SQLite database (`Booktracker.db`).
2.  Database schema initially based on the provided ERD, including `User` and `ReadingHabit` tables. *(See Implemented Enhancements below for schema improvements)*.
3.  Population of the `User` table via `seed.sql` and the original `ReadingHabit` table via CSV import (steps documented in `data/normalization.sql`).
4.  Implementation of all required functionalities using SQL for calculations where appropriate:
    * [1] Add a new user.
    * [2] Display all reading habits for a specific user.
    * [3] Update the title of a book.
    * [4] Delete a specific reading habit record by its ID.
    * [5] Calculate and display the mean age of all users.
    * [6] Count and display the total number of distinct users who have read a specific book.
    * [7] Calculate and display the total number of pages read across all users and habits.
    * [8] Count and display the total number of users who have read more than one distinct book.
    * [9] Dynamically add the `Name` column (TEXT) to the `User` table on application startup if it doesn't exist.
5.  Refactored user interface for clearer presentation (headers, status messages, separators). *(Self-correction: Added UI improvement as part of MVP effort)*

## Implemented Enhancements (Beyond MVP)

* **Database Normalization:** The database schema was enhanced by creating a separate `Books` table (`bookID` PK, `title` UNIQUE) and modifying the `ReadingHabit` table to use a `bookID` foreign key. This reduces data redundancy and improves data integrity. The SQL migration steps are documented in `data/normalization.sql`.
* **DAO Pattern Implementation:** The database interaction logic was refactored from `Main.java` into separate Data Access Object (DAO) classes (`UserDao`, `BookDao`, `ReadingHabitDao`) and corresponding Model classes (`User`, `Book`, `ReadingHabit`). This improves code organization, maintainability, and separation of concerns.

## Potential Future Enhancements

While the current version meets the assignment requirements and includes significant enhancements, the following features could be implemented in the future:

**Database Improvements:**

* **Data Constraints:** Add more specific SQL constraints (e.g., `CHECK(age > 0)`).
* **Book Author:** Add an `author` column to the `Books` table.

**Enhanced Functionality:**

* **Add Reading Habit:** Implement a menu option (#10) to manually add a new reading habit record (handling `bookID` lookup/creation).
* **List All Users/Books:** Add options to display all users or all books.
* **Advanced Statistics:** Implement more complex SQL queries (e.g., most read book, users with no habits).
* **Search Functionality:** Allow searching for users by name or books by title.
* **Data Validation:** Add more robust input validation in Java (e.g., check if `userID` exists before adding/fetching habits).

**Code Quality & Structure:**

* **Transaction Management:** Implement explicit transaction control (`setAutoCommit(false)`, `commit()`, `rollback()`) for relevant operations.
* **Unit Testing:** Introduce JUnit tests for DAO methods.

**User Interface (CLI):**

* **Improved Output Formatting:** Implement pagination for long lists of results.
* **Screen Clearing:** Attempt OS-specific screen clearing.