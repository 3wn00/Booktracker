-- Creating a new table for books to normalize the database
CREATE TABLE Books (
    bookID INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL UNIQUE
);

-- Inserting distinct book titles from ReadingHabit into the Books table
INSERT INTO Books (title)
SELECT DISTINCT book
FROM ReadingHabit;

-- Ddd the bookID column to ReadingHabit and set it to reference the Books table
ALTER TABLE ReadingHabit
ADD COLUMN bookID INTEGER REFERENCES Books(bookID);

-- Update the bookID column with the corresponding bookID from the Books table
UPDATE ReadingHabit
SET bookID = (SELECT b.bookID FROM Books b WHERE b.title = ReadingHabit.book);