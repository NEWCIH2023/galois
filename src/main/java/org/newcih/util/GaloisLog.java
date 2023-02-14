package org.newcih.util;

public class GaloisLog {

    public static GaloisLog getLogger(Class<?> clazz) {
        return new GaloisLog();
    }

    public void info(String msg, Object... args) {
        System.out.printf(msg + "\n", args);
    }

    public void debug(String msg, Object... args) {
        System.out.printf(msg + "\n", args);
    }

    public void error(String msg, Object... args) {
        System.out.printf(msg + "\n", args);
    }

    public void error(String msg, Throwable throwable) {
        System.err.println(msg + "\n" + throwable.getMessage());
    }

}
