package com.finpeer.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/** Loads db.properties and provides JDBC connections. */
public class DBConnection {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {
            System.err.println("Could not load db.properties: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = value("db.url", "FINPEER_DB_URL");
        String username = value("db.username", "FINPEER_DB_USERNAME");
        String password = value("db.password", "FINPEER_DB_PASSWORD");
        if (url == null || url.isBlank() || username == null || username.isBlank()) {
            throw new SQLException("Database is not configured. Set db.properties or FINPEER_DB_URL and FINPEER_DB_USERNAME.");
        }
        return DriverManager.getConnection(url, username, password == null ? "" : password);
    }

    private static String value(String property, String environmentVariable) {
        String systemValue = System.getProperty(property);
        if (systemValue != null && !systemValue.isBlank()) return systemValue;
        String configuredValue = props.getProperty(property);
        if (configuredValue != null && !configuredValue.isBlank()) return configuredValue;
        return System.getenv(environmentVariable);
    }
}
