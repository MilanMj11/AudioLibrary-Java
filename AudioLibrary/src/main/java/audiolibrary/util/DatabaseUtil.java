package audiolibrary.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/audiolibrary";
    private static final String JDBC_USER = "audio_user";
    private static final String JDBC_PASSWORD = "milan";

    /**
     * Establishes a connection to the database.
     * @return the connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}
