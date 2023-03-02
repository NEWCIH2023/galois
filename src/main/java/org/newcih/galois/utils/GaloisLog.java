package org.newcih.galois.utils;

import org.newcih.galois.conf.GlobalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.event.Level;

import java.util.Optional;

import static org.newcih.galois.constants.ConfConstant.LOGGING_ENABLED;
import static org.newcih.galois.constants.ConfConstant.LOGGING_LEVEL;

public class GaloisLog implements Logger {

    private final Logger logger;

    private final boolean loggingEnabled = GlobalConfiguration.getBoolean(LOGGING_ENABLED);

    private final static Level loggingLevel;

    static {
        String level = Optional.ofNullable(GlobalConfiguration.getString(LOGGING_LEVEL))
                .orElse(Level.ERROR.toString());
        level = level.toUpperCase();
        loggingLevel = Level.valueOf(level);
    }

    public GaloisLog(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public static GaloisLog getLogger(Class<?> clazz) {
        return new GaloisLog(clazz);
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return loggingLevel.toInt() <= Level.TRACE.toInt() && loggingEnabled;
    }

    @Override
    public void trace(String msg) {
        if (isTraceEnabled()) {
            logger.trace(msg);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            logger.trace(format, arg);
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            logger.trace(format, arg1, arg2);
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) {
            logger.trace(format, arguments);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (isTraceEnabled()) {
            logger.trace(msg, t);
        }
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (isTraceEnabled()) {
            logger.trace(marker, msg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (isTraceEnabled()) {
            logger.trace(marker, format, arg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            logger.trace(marker, format, arg1, arg2);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if (isTraceEnabled()) {
            logger.trace(marker, format, argArray);
        }
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (isTraceEnabled()) {
            logger.trace(marker, msg, t);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return loggingLevel.toInt() <= Level.DEBUG.toInt() && loggingEnabled;
    }

    @Override
    public void debug(String msg) {
        if (isDebugEnabled()) {
            logger.debug(msg);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) {
            logger.debug(format, arg);
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            logger.debug(format, arg1, arg2);
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled()) {
            logger.debug(format, arguments);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (isDebugEnabled()) {
            logger.debug(msg, t);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (isDebugEnabled()) {
            logger.debug(marker, msg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (isDebugEnabled()) {
            logger.debug(marker, format, arg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            logger.debug(marker, format, arg1, arg2);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (isDebugEnabled()) {
            logger.debug(marker, format, arguments);
        }
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (isDebugEnabled()) {
            logger.debug(marker, msg, t);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return loggingLevel.toInt() <= Level.INFO.toInt() && loggingEnabled;
    }

    @Override
    public void info(String msg) {
        if (isInfoEnabled()) {
            logger.info(msg);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (isInfoEnabled()) {
            logger.info(format, arg);
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            logger.info(format, arg1, arg2);
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isInfoEnabled()) {
            logger.info(format, arguments);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (isInfoEnabled()) {
            logger.info(msg, t);
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String msg) {
        if (isInfoEnabled()) {
            logger.info(marker, msg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (isInfoEnabled()) {
            logger.info(marker, format, arg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            logger.info(marker, format, arg1, arg2);
        }
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        if (isInfoEnabled()) {
            logger.info(marker, format, arguments);
        }
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (isInfoEnabled()) {
            logger.info(marker, msg, t);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return loggingLevel.toInt() <= Level.WARN.toInt() && loggingEnabled;
    }

    @Override
    public void warn(String msg) {
        if (isWarnEnabled()) {
            logger.warn(msg);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (isWarnEnabled()) {
            logger.warn(format, arg);
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isWarnEnabled()) {
            logger.warn(format, arguments);
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (isWarnEnabled()) {
            logger.warn(format, arg1, arg2);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (isWarnEnabled()) {
            logger.warn(msg, t);
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (isWarnEnabled()) {
            logger.warn(marker, msg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (isWarnEnabled()) {
            logger.warn(marker, format, arg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (isWarnEnabled()) {
            logger.warn(marker, format, arg1, arg2);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        if (isWarnEnabled()) {
            logger.warn(marker, format, arguments);
        }
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (isWarnEnabled()) {
            logger.warn(marker, msg, t);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return loggingLevel.toInt() <= Level.ERROR.toInt() && loggingEnabled;
    }

    @Override
    public void error(String msg) {
        if (isErrorEnabled()) {
            logger.error(msg);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (isErrorEnabled()) {
            logger.error(format, arg);
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) {
            logger.error(format, arg1, arg2);
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isErrorEnabled()) {
            logger.error(format, arguments);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (isErrorEnabled()) {
            logger.error(msg, t);
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String msg) {
        if (isErrorEnabled()) {
            logger.error(marker, msg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (isErrorEnabled()) {
            logger.error(marker, format, arg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) {
            logger.error(marker, format, arg1, arg2);
        }
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        if (isErrorEnabled()) {
            logger.error(marker, format, arguments);
        }
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (isErrorEnabled()) {
            logger.error(marker, msg, t);
        }
    }

}
