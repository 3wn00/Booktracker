-- data/schema.sql

DROP TABLE IF EXISTS ReadingHabit;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Book;

CREATE TABLE User (
    userID INTEGER PRIMARY KEY AUTOINCREMENT,
    Name TEXT NOT NULL,
    Age INTEGER
);

CREATE TABLE Book (
    bookID INTEGER PRIMARY KEY AUTOINCREMENT,
    Title TEXT NOT NULL UNIQUE
);

CREATE TABLE ReadingHabit (
    habitID INTEGER PRIMARY KEY,
    userID INTEGER NOT NULL,
    bookID INTEGER NOT NULL,
    pagesRead INTEGER DEFAULT 0,
    submissionMoment TEXT,
    FOREIGN KEY (userID) REFERENCES User(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (bookID) REFERENCES Book(bookID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_readinghabit_user ON ReadingHabit (userID);
CREATE INDEX idx_readinghabit_book ON ReadingHabit (bookID);
CREATE INDEX idx_book_title ON Book (Title);