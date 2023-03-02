package org.newcih.galois.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * global configuration service
 */
public class GlobalConfiguration {

    private static final Properties configuration = new Properties();

    /**
     * load global configuration from galois.properties file
     */
    static {
        try (InputStream is = GlobalConfiguration.class.getClassLoader().getResourceAsStream("galois.properties")) {
            configuration.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get string type property value
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        String result = configuration.getProperty(key);
        return Optional.ofNullable(result).orElse("");
    }

    /**
     * get boolean type property value
     *
     * @param key
     * @return
     */
    public static boolean getBoolean(String key) {
        String result = getString(key);
        return "true".equalsIgnoreCase(result);
    }

    /**
     * get long type property value
     *
     * @param key
     * @return
     */
    public static long getLong(String key) {
        String result = getString(key);
        try {
            return Long.parseLong(result);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * get int type property value
     *
     * @param key
     * @return
     */
    public static int getInteger(String key) {
        String result = getString(key);
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {
            return 0;
        }
    }

}
