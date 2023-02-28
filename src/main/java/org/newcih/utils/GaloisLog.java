package org.newcih.utils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.*;

/**
 * galois log service
 *
 * @author liuguangsheng
 */
public class GaloisLog extends Logger {

    protected static final String MARKER = "Galois";

    private static final Handler consoleHandler = new ConsoleHandler();

    static {
        consoleHandler.setLevel(Level.CONFIG);
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                Date logDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                logDate.setTime(record.getMillis());
                return String.format("[%s] %s [%s] %s%n", MARKER, sdf.format(logDate), record.getLevel(),
                        record.getMessage());
            }
        };
        consoleHandler.setFormatter(formatter);
    }

    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @param name               A name for the logger.  This should
     *                           be a dot-separated name and should normally
     *                           be based on the package name or class name
     *                           of the subsystem, such as java.net
     *                           or javax.swing.  It may be null for anonymous Loggers.
     * @param resourceBundleName name of ResourceBundle to be used for localizing
     *                           messages for this logger.  May be null if none
     *                           of the messages require localization.
     */
    protected GaloisLog(String name, String resourceBundleName) {
        super(name, resourceBundleName);
        addHandler(consoleHandler);
    }

    public static GaloisLog getLogger(Class<?> clazz) {
        return new GaloisLog(clazz.getName(), null);
    }

    public boolean isInfoEnabled() {
        return consoleHandler.getLevel().intValue() >= Level.INFO.intValue();
    }

    public void info(String msg, Object... params) {
        super.info(String.format(msg, params));
    }

    public boolean isDebugEnabled() {
        return consoleHandler.getLevel().intValue() >= Level.CONFIG.intValue();
    }

    public void debug(String msg, Object... params) {
        super.config(String.format(msg, params));
    }


    public boolean isWarnEnabled() {
        return consoleHandler.getLevel().intValue() >= Level.WARNING.intValue();
    }

    public void warn(String msg, Object... params) {
        super.warning(String.format(msg, params));
    }

    public boolean isErrorEnabled() {
        return consoleHandler.getLevel().intValue() >= Level.SEVERE.intValue();
    }

    public void error(String msg, Object... params) {
        Throwable throwable = null;
        List<Object> objects = new ArrayList<>(16);
        Collections.addAll(objects, params);
        Iterator<Object> iterator = objects.listIterator();
        Object temp;

        while (iterator.hasNext()) {
            temp = iterator.next();
            if (temp instanceof Throwable) {
                throwable = (Throwable) temp;
                iterator.remove();
            }
        }

        super.severe(String.format(msg, objects.toArray()));

        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
}
