// Represents a ReadingHabit entity
// Includes bookTitle for convenience after joining
public class ReadingHabit {
    private int habitID;
    private int userID; // Foreign key to User
    private int bookID; // Foreign key to Book
    private int pagesRead;
    private String submissionMoment;
    private String bookTitle; // Added to hold the title after joining

    // Constructor - including bookTitle fetched from join
    public ReadingHabit(int habitID, int userID, int bookID, int pagesRead, String submissionMoment, String bookTitle) {
        this.habitID = habitID;
        this.userID = userID;
        this.bookID = bookID;
        this.pagesRead = pagesRead;
        this.submissionMoment = submissionMoment;
        this.bookTitle = bookTitle; // Store the joined title
    }

    // Getters
    public int getHabitID() {
        return habitID;
    }

    public int getUserID() {
        return userID;
    }

    public int getBookID() {
        return bookID;
    }

    public int getPagesRead() {
        return pagesRead;
    }

    public String getSubmissionMoment() {
        return submissionMoment;
    }

    public String getBookTitle() {
        return bookTitle; // Getter for the joined title
    }

    @Override
    public String toString() {
        return "ReadingHabit [habitID=" + habitID + ", userID=" + userID + ", bookID=" + bookID +
               ", pagesRead=" + pagesRead + ", submissionMoment=" + submissionMoment +
               ", bookTitle=" + bookTitle + "]";
    }
}