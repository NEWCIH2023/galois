package io.liuguangsheng.galois.conf;

import io.liuguangsheng.galois.constants.Constant;
import io.liuguangsheng.galois.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * global configuration service
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class GlobalConfiguration {

    private static final String GALOIS_PROPERTIES = "galois.properties";
    private static final ConcurrentHashMap<String, String> configuration = new ConcurrentHashMap<>();

    private static class GlobalConfigurationHolder {
        private static final GlobalConfiguration globalConfiguration = new GlobalConfiguration();
    }

    private GlobalConfiguration() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            throw new IllegalStateException("ClassLoader is null");
        }
        try (InputStream is = loader.getResourceAsStream(GALOIS_PROPERTIES)) {
            if (is == null) {
                throw new IOException("Configuration file not found: " + GALOIS_PROPERTIES);
            }
            Properties props = new Properties();
            props.load(is);
            props.stringPropertyNames().forEach(key -> configuration.put(key, props.getProperty(key)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /**
     * get instance
     *
     * @return {@link GlobalConfiguration}
     * @see GlobalConfiguration
     */
    public static GlobalConfiguration getInstance() {
        return GlobalConfigurationHolder.globalConfiguration;
    }

    /**
     * get string
     *
     * @param key key
     * @return {@link String}
     * @see String
     */
    public String getStr(String key) {
        return getStr(key, Constant.EMPTY);
    }

    /**
     * get string
     *
     * @param key          key
     * @param defaultValue defaultValue
     * @return {@link String}
     * @see String
     */
    public String getStr(String key, String defaultValue) {
        if (StringUtil.isBlank(key)) {
            return defaultValue;
        }

        String result = configuration.getOrDefault(key, System.getProperty(key, defaultValue));
        return StringUtil.isBlank(result) ? defaultValue : result;
    }

    /**
     * get boolean
     *
     * @param key key
     * @return {@link boolean}
     */
    public boolean getBool(String key) {
        return getBool(key, false);
    }

    /**
     * get boolean
     *
     * @param key          key
     * @param defaultValue defaultValue
     * @return {@link boolean}
     */
    public boolean getBool(String key, boolean defaultValue) {
        String result = getStr(key, Boolean.toString(defaultValue));
        return Boolean.parseBoolean(result);
    }

    /**
     * get long
     *
     * @param key key
     * @return {@link long}
     */
    public long getLong(String key) {
        return getLong(key, 0L);
    }

    /**
     * get long
     *
     * @param key          key
     * @param defaultValue defaultValue
     * @return {@link long}
     */
    public long getLong(String key, long defaultValue) {
        String result = getStr(key, String.valueOf(defaultValue));
        try {
            return Long.parseLong(result);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * get integer
     *
     * @param key key
     * @return {@link int}
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * get integer
     *
     * @param key          key
     * @param defaultValue defaultValue
     * @return {@link int}
     */
    public int getInt(String key, int defaultValue) {
        String result = getStr(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
