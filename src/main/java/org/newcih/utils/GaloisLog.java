package org.newcih.utils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class GaloisLog {

    public static final String LOG_LEVEL = "log.level";
    public static final String LOG_LEVEL_INFO = "info", LOG_LEVEL_DEBUG = "debug", LOG_LEVEL_WARN = "warn", LOG_LEVEL_ERROR = "error";
    public static final int LOG_LEVEL_INFO_RANK = 0, LOG_LEVEL_DEBUG_RANK = -1, LOG_LEVEL_WARN_RANK = 5, LOG_LEVEL_ERROR_RANK = 10;
    public static final Map<String, Integer> LOG_LEVEL_RANK_MAP = new HashMap<>();
    public static final String MARKER = "## Galois ##";
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final ConcurrentHashMap<String, GaloisLog> LOGGER_MAP = new ConcurrentHashMap<>(256);
    private static final Properties LOG_PROPERTIES;

    static {
        LOG_LEVEL_RANK_MAP.put(LOG_LEVEL_INFO, LOG_LEVEL_INFO_RANK);
        LOG_LEVEL_RANK_MAP.put(LOG_LEVEL_DEBUG, LOG_LEVEL_DEBUG_RANK);
        LOG_LEVEL_RANK_MAP.put(LOG_LEVEL_WARN, LOG_LEVEL_WARN_RANK);
        LOG_LEVEL_RANK_MAP.put(LOG_LEVEL_ERROR, LOG_LEVEL_ERROR_RANK);
    }

    static {
        try (InputStream logFile = GaloisLog.class.getResourceAsStream("/galois-log.properties")) {
            LOG_PROPERTIES = new Properties();
            LOG_PROPERTIES.load(logFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static GaloisLog getLogger(Class<?> clazz) {
        if (LOGGER_MAP.containsKey(clazz.getName())) {
            return LOGGER_MAP.get(clazz.getName());
        } else {
            GaloisLog temp = new GaloisLog();
            LOGGER_MAP.put(clazz.getName(), temp);
            return temp;
        }
    }

    private static int currentLogRank() {
        return LOG_LEVEL_RANK_MAP.get(LOG_PROPERTIES.getProperty(LOG_LEVEL));
    }

    public void info(String msg, Object... args) {
        if (currentLogRank() <= LOG_LEVEL_INFO_RANK) {
            System.out.printf(LocalDateTime.now().format(DATE_TIME_FORMAT) + " " + MARKER + " [INFO] " + msg + "\n", args);
        }
    }

    public boolean isInfoEnabled() {
        return currentLogRank() <= LOG_LEVEL_INFO_RANK;
    }

    public void debug(String msg, Object... args) {
        if (currentLogRank() <= LOG_LEVEL_DEBUG_RANK) {
            System.out.printf(LocalDateTime.now().format(DATE_TIME_FORMAT) + " " + MARKER + " [DEBUG] " + msg + "\n", args);
        }
    }

    public boolean isDebugEnabled() {
        return currentLogRank() <= LOG_LEVEL_DEBUG_RANK;
    }

    public void error(String msg, Object... args) {
        if (currentLogRank() <= LOG_LEVEL_ERROR_RANK) {
            System.out.printf(LocalDateTime.now().format(DATE_TIME_FORMAT) + " " + MARKER + " [ERROR] " + msg + "\n", args);
        }
    }

    public void error(String msg, Throwable throwable) {
        if (currentLogRank() <= LOG_LEVEL_ERROR_RANK) {
            System.err.println(LocalDateTime.now().format(DATE_TIME_FORMAT) + " " + MARKER + " [ERROR] " + msg + "\n");
            throwable.printStackTrace();
        }
    }

    public boolean isErrorEnabled() {
        return currentLogRank() <= LOG_LEVEL_ERROR_RANK;
    }

}
