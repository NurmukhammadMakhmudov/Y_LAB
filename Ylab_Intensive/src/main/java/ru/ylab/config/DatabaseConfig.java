package ru.ylab.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static DataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = createDataSource();
        }
        return dataSource;
    }

    private static DataSource createDataSource() {
        String url = System.getProperty("db.url");
        String username = System.getProperty("db.username");
        String password = System.getProperty("db.password");

        if (url == null || username == null || password == null) {
            url = getPropertyFromFile("db.url");
            username = getPropertyFromFile("db.username");
            password = getPropertyFromFile("db.password");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        return new HikariDataSource(config);
    }

    private static String getPropertyFromFile(String key) {
        try (var input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            java.util.Properties props = new java.util.Properties();
            if (input != null) {
                props.load(input);
            }
            return props.getProperty(key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load property: " + key, e);
        }
    }

    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    public static void closeDataSource() {
        if (dataSource != null && dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
            dataSource = null;
        }
    }
}
