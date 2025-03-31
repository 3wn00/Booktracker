import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Utility class for handling database connections
public class DatabaseConnector {

    private static final String DB_URL = "jdbc:sqlite:Booktracker.db";

    // Static block to ensure driver is loaded only once
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("FATAL ERROR: SQLite JDBC driver not found. Make sure sqlite-jdbc-*.jar is in the classpath.");
            // In a real app, might handle this more gracefully or log it centrally
             throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}