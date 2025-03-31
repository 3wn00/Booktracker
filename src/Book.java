// Represents a Book entity
public class Book {
    private int bookID;
    private String title;
    // Could add author later

    // Constructor
    public Book(int bookID, String title) {
        this.bookID = bookID;
        this.title = title;
    }

    // Getters
    public int getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

     // Setter for title might be useful if updating via object
     public void setTitle(String title) {
         this.title = title;
     }

    @Override
    public String toString() {
        return "Book [bookID=" + bookID + ", title=" + title + "]";
    }
}