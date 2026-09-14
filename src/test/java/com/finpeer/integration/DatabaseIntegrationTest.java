package com.finpeer.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import com.finpeer.util.DBConnection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import org.junit.jupiter.api.Test;

class DatabaseIntegrationTest {
    @Test
    void connectsToConfiguredDatabase() throws Exception {
        boolean configured = (System.getenv("FINPEER_DB_URL") != null && System.getenv("FINPEER_DB_USERNAME") != null)
                || (System.getProperty("db.url") != null && System.getProperty("db.username") != null);
        assumeTrue(configured, "Set FINPEER_DB_URL and FINPEER_DB_USERNAME to run the MySQL integration test");
        try (Connection connection = DBConnection.getConnection()) {
            assertFalse(connection.isClosed());
            DatabaseMetaData metadata = connection.getMetaData();
            assertNotNull(metadata.getDatabaseProductName());
        }
    }
}
