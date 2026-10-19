package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads src/test/resources/config.properties.
 * A JVM system property with the same key (e.g. -Dbrowser=chrome) takes priority.
 */
public class ConfigReader {

    private static final String RESOURCES_DIR = "src/test/resources/";

    private static final Properties prop = new Properties();

    static {
        try (InputStream in = openResource(RESOURCES_DIR + "config.properties")) {
            prop.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load config.properties", e);
        }
    }

    private ConfigReader() {
    }

    /**
     * Opens a test resource such as "src/test/resources/testdata/LocationTestData.xlsx".
     * Uses the file if it exists relative to the working directory, otherwise loads it
     * from the test classpath. Eclipse can run tests from a different working
     * directory, so the classpath lookup keeps those runs working.
     */
    public static InputStream openResource(String path) throws IOException {
        File file = new File(path);
        if (file.isFile()) {
            return new FileInputStream(file);
        }
        String classpathName = path.replace('\\', '/');
        if (classpathName.startsWith(RESOURCES_DIR)) {
            classpathName = classpathName.substring(RESOURCES_DIR.length());
        }
        InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(classpathName);
        if (in == null) {
            throw new FileNotFoundException(path + " not found in " + new File("").getAbsolutePath()
                    + " or on the classpath as " + classpathName);
        }
        return in;
    }

    public static String getProperty(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override.trim();
        }
        return prop.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
}
