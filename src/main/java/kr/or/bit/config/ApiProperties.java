package kr.or.bit.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ApiProperties {
    private static final String RESOURCE_NAME = "api.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ApiProperties() {
    }

    public static String getRequired(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required API property: " + key);
        }
        return value.trim();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = ApiProperties.class.getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
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
