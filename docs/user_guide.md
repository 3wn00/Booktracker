# Booktracker Application - User Guide

## Introduction

Welcome to the Booktracker command-line administration tool. This application allows you to manage user information and reading habit data stored in the associated SQLite database (`Booktracker.db`).

## Prerequisites

* **Java Development Kit (JDK):** Ensure you have JDK 8 or higher installed.
* **SQLite JDBC Driver:** The necessary driver (`sqlite-jdbc-....jar`) should be present in the `lib` folder of the project.
* **Database File:** A `Booktracker.db` SQLite database file must be present in the project's root directory. See Setup below.

## Setup

1.  **Get the Code:** Obtain the project files, either by cloning the GitHub repository or extracting the provided Zip file.
2.  **Database Setup:**
    * A `Booktracker.db` file should be included, potentially already populated.
    * **If setting up from scratch:** You may need to create the database structure and populate initial data. This typically involves:
        * Using a tool like DB Browser for SQLite to execute `data/seed.sql` [cite: uploaded:Booktracker/data/seed.sql] (to create and populate the `User` table).
        * Applying the normalization script `data/normalization.sql` (if provided and applicable) to create the `Books` table and modify `ReadingHabit`.
        * Importing data from `data/reading_habits.csv` [cite: uploaded:Booktracker/data/reading_habits.csv] into the `ReadingHabit` table using DB Browser for SQLite (File -> Import -> Table from CSV file...).
    * Refer to the main `README.md` file [cite: uploaded:Booktracker/README.md] for potentially more detailed database setup instructions.

## Running the Application

You can run the application in a couple of ways:

* **Using VS Code (Recommended):**
    1.  Open the `Booktracker` project folder in VS Code.
    2.  Ensure the Java Extension Pack is installed.
    3.  Navigate to `src/Main.java` [cite: uploaded:Booktracker/src/Main.java].
    4.  Click the "Run" button (▶️) that appears above the `public static void main(String[] args)` method.

* **Using Command Line / Terminal:**
    1.  Open your terminal or command prompt.
    2.  Navigate to the root directory of the `Booktracker` project.
    3.  **Compile (if needed):**
        * Windows: `javac -cp ".;lib/sqlite-jdbc-....jar" -d out src/*.java`
        * macOS/Linux: `javac -cp ".:lib/sqlite-jdbc-....jar" -d out src/*.java`
        *(Note: Replace `....` with the actual version number of your JAR file. Compiling all .java files ensures DAOs/Models are included)*
    4.  **Run:**
        * Windows: `java -cp "out;lib/sqlite-jdbc-....jar" Main`
        * macOS/Linux: `java -cp "out:lib/sqlite-jdbc-....jar" Main`

## Using the Application

Once the application starts, you will see the main menu:

======== Booktracker Admin Menu ========

Add User
Show User Habits
Change Book Title
Delete Reading Habit
Show Mean User Age
Show User Count for Book
Show Total Pages Read
Show Users Reading >1 Book
(Info) Add 'Name' column (startup)
Exit Application ======================================== Enter your choice:

Enter the number corresponding to the action you want to perform and press Enter. Follow the on-screen prompts for each action:

* **1. Add User:** Enter the details for a new user (ID, Age, Gender, Name).
* **2. Show User Habits:** Enter a User ID to see their recorded reading habits, including book title, pages read, and timestamp.
* **3. Change Book Title:** Enter the current book title and the desired new title. This updates the central `Books` table.
* **4. Delete Reading Habit:** Enter the unique `habitID` of the specific reading record you wish to remove.
* **5. Show Mean User Age:** Displays the calculated average age of all users in the database.
* **6. Show User Count for Book:** Enter a book title to see how many distinct users have read it.
* **7. Show Total Pages Read:** Displays the sum of pages read across all users and all habits.
* **8. Show Users Reading >1 Book:** Displays the count of users who have recorded reading more than one distinct book title.
* **9. (Info):** Just confirms that a startup check for the 'Name' column in the User table was performed. No action needed.
* **0. Exit:** Stops the application.

The application will loop back to the main menu after each action (except Exit).