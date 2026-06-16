package kr.or.bit.config;

import kr.or.bit.utils.DBType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public final class DatabaseProperties {
    private static final String RESOURCE_NAME = "db.properties";
    private static final Properties PROPERTIES = loadProperties();

    private DatabaseProperties() {
    }

    public static String getRequired(DBType dbType, String name) {
        String key = "db." + dbType.name().toLowerCase(Locale.ROOT) + "." + name;
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required database property: " + key);
        }
        return value.trim();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = DatabaseProperties.class.getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
            if (input == null) {
                throw new IllegalStateException(RESOURCE_NAME + " was not found on the classpath.");
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + RESOURCE_NAME + ".", e);
        }
    }
}
