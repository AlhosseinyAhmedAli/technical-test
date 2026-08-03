package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Small helper for reading per-suite defaults out of {@code <section>/data.properties}
 * on the test classpath (e.g. {@code api/data.properties}, {@code mobile/data.properties}).
 *
 * <p>Values are cached per section after the first load. If the section's properties
 * file can't be found (or doesn't define the requested key), {@link #get} falls back
 * to the supplied default rather than throwing, so tests keep working even without a
 * properties file present.</p>
 */
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
