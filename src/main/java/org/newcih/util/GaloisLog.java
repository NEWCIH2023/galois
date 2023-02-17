package org.newcih.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GaloisLog {

    public static final String MARKER = "## Galois ##";

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_TIME;

    public static GaloisLog getLogger(Class<?> clazz) {
        return new GaloisLog();
    }

    public void info(String msg, Object... args) {
        System.out.printf(LocalDateTime.now().format(DATE_TIME_FORMAT) + " " + MARKER + " [INFO] " + msg + "\n", args);
    }

    public void debug(String msg, Object... args) {
        System.out.printf(LocalDateTime.now().format(DATE_TIME_FORMAT) + " " + MARKER + " [DEBUG] " + msg + "\n", args);
    }

    public void error(String msg, Object... args) {
        System.out.printf(LocalDateTime.now().format(DATE_TIME_FORMAT) + " " + MARKER + " [ERROR] " + msg + "\n", args);
    }

    public void error(String msg, Throwable throwable) {
        System.err.println(LocalDateTime.now().format(DATE_TIME_FORMAT) + " " + MARKER + " [ERROR] " + msg + "\n" + throwable.getMessage());
    }

}
