package test.integration;

import com.sports.SportsManagementApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SportsManagementApplication.class)
@ActiveProfiles("test")
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testPostgresConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "PostgreSQL connection should not be null");
            assertFalse(connection.isClosed(), "PostgreSQL connection should be open");

            String url = connection.getMetaData().getURL();
            assertTrue(url.contains("postgresql"), "URL should contain 'postgresql'");

            System.out.println("=== DATABASE CONNECTION TEST ===");
            System.out.println("Connection URL: " + url);
            System.out.println("Database Product: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("Database Version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver Name: " + connection.getMetaData().getDriverName());
            System.out.println("================================");
        }
    }

    @Test
    public void testConfigurationLoaded() {
        String postgresUrl = System.getProperty("spring.datasource.url");
        String mongoUri = System.getProperty("spring.data.mongodb.uri");

        System.out.println("=== CONFIGURATION TEST ===");
        System.out.println("PostgreSQL URL from System Property: " + (postgresUrl != null ? "SET" : "NOT SET"));
        System.out.println("MongoDB URI from System Property: " + (mongoUri != null ? "SET" : "NOT SET"));
        System.out.println("==========================");

        assertNotNull(dataSource, "DataSource should be available");
    }
}