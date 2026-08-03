package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class TestData {

    private static final ConcurrentMap<String, Properties> CACHE = new ConcurrentHashMap<>();

    private TestData() {
    }

    public static String get(String section, String key, String defaultValue) {
        Properties properties = CACHE.computeIfAbsent(section, TestData::load);
        String value = properties.getProperty(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private static Properties load(String section) {
        Properties properties = new Properties();
        String resource = section + "/data.properties";
        try (InputStream in = TestData.class.getClassLoader().getResourceAsStream(resource)) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            // Fall through with an empty Properties instance; callers supply defaults.
        }
        return properties;
    }
}
