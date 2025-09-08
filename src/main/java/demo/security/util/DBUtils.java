package demo.security.util;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    Connection connection;
    public DBUtils() throws SQLException {
        // SECURITY FIX: Remove hard-coded credentials, use environment variables or config
        String jdbcUrl = System.getenv("DB_URL");
        String jdbcUser = System.getenv("DB_USER");
        String jdbcPassword = System.getenv("DB_PASSWORD");
        
        // Fallback to properties file or configuration if environment variables not set
        if (jdbcUrl == null || jdbcUser == null || jdbcPassword == null) {
            // In production, load from encrypted configuration file
            throw new SQLException("Database configuration not found. Set DB_URL, DB_USER, and DB_PASSWORD environment variables.");
        }
        
        connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    public List<String> findUsers(String user) throws Exception {
        String query = "SELECT userid FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> users = new ArrayList<>();
                while (resultSet.next()) {
                    users.add(resultSet.getString(1)); // Fixed: ResultSet indices start at 1
                }
                return users;
            }
        }
    }

    public List<String> findItem(String itemId) throws Exception {
        String query = "SELECT item_id FROM items WHERE item_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, itemId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> items = new ArrayList<>();
                while (resultSet.next()) {
                    items.add(resultSet.getString(1)); // Fixed: ResultSet indices start at 1
                }
                return items;
            }
        }
    }
}
