package infrastructure.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotenvLoader {

    private static final Logger logger = LoggerFactory.getLogger(DotenvLoader.class);

    private static Dotenv dotenv;

    @PostConstruct
    public void init() {
        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            logger.info("=== DOTENV CONFIGURATION LOADED ===");
            logger.info("PostgreSQL URL: {}", getenv("POSTGRES_JDBC_URL", "NOT SET"));
            logger.info("PostgreSQL User: {}", getenv("POSTGRES_USERNAME", "NOT SET"));
            logger.info("PostgreSQL Password: *** (hidden)");
            logger.info("MongoDB URI: {}", getenv("MONGODB_URI", "NOT SET"));
            logger.info("MongoDB Database: {}", getenv("MONGODB_DATABASE", "NOT SET"));
            logger.info("===================================");

            applyToSystemProperties();

        } catch (Exception e) {
            logger.warn("Could not load .env file, using default application.yml values: {}", e.getMessage());
        }
    }

    private void applyToSystemProperties() {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("POSTGRES_JDBC_URL", "spring.datasource.url");
        mappings.put("POSTGRES_USERNAME", "spring.datasource.username");
        mappings.put("POSTGRES_PASSWORD", "spring.datasource.password");
        mappings.put("MONGODB_URI", "spring.data.mongodb.uri");
        mappings.put("MONGODB_DATABASE", "spring.data.mongodb.database");

        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            String envValue = getenv(entry.getKey());
            if (envValue != null && !envValue.isEmpty()) {
                System.setProperty(entry.getValue(), envValue);
                logger.debug("Applied {} = {}", entry.getValue(), envValue);
            }
        }
    }

    public static String getenv(String key) {
        return getenv(key, null);
    }

    public static String getenv(String key, String defaultValue) {
        if (dotenv != null) {
            String value = dotenv.get(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        return defaultValue;
    }
}