/*
 * MIT License
 *
 * Copyright (c) [2023] [liuguangsheng]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.liuguangsheng.galois.utils;

import static io.liuguangsheng.galois.constants.ConfConstant.BUILD_TYPE;
import static io.liuguangsheng.galois.constants.Constant.RELEASE;
import io.liuguangsheng.galois.conf.GlobalConfiguration;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * The type Galois log.
 */
public class GaloisLog implements Logger {

  private final Logger logger;
  private final boolean isReleaseVersion = Objects.equals(
      GlobalConfiguration.getInstance().getString(BUILD_TYPE),
      RELEASE);

  /**
   * Instantiates a new Galois log.
   *
   * @param clazz the clazz
   */
  public GaloisLog(Class<?> clazz) {
    logger = LoggerFactory.getLogger(clazz);
  }

  /**
   * Instantiates a new Galois log.
   *
   * @param name the name
   */
  public GaloisLog(String name) {
    logger = LoggerFactory.getLogger(name);
  }

  @Override
  public String getName() {
    return logger.getName();
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public void trace(String msg) {
    logger.trace(msg);
  }

  @Override
  public void trace(String format, Object arg) {
    logger.trace(format, arg);
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    logger.trace(format, arg1, arg2);
  }

  @Override
  public void trace(String format, Object... arguments) {
    logger.trace(format, arguments);
  }

  @Override
  public void trace(String msg, Throwable t) {
    if (isReleaseVersion) {
      logger.trace(msg, t);
    } else {
      logger.trace(msg);
    }
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return logger.isTraceEnabled(marker);
  }

  @Override
  public void trace(Marker marker, String msg) {
    logger.trace(marker, msg);

  }

  @Override
  public void trace(Marker marker, String format, Object arg) {
    logger.trace(marker, format, arg);

  }

  @Override
  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    logger.trace(marker, format, arg1, arg2);

  }

  @Override
  public void trace(Marker marker, String format, Object... argArray) {
    logger.trace(marker, format, argArray);

  }

  @Override
  public void trace(Marker marker, String msg, Throwable t) {
    if (isReleaseVersion) {
      logger.trace(marker, msg, t);
    } else {
      logger.trace(marker, msg);
    }

  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public void debug(String msg) {
    logger.debug(msg);
  }

  @Override
  public void debug(String format, Object arg) {
    logger.debug(format, arg);

  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    logger.debug(format, arg1, arg2);

  }

  @Override
  public void debug(String format, Object... arguments) {
    logger.debug(format, arguments);

  }

  @Override
  public void debug(String msg, Throwable t) {
    if (isReleaseVersion) {
      logger.debug(msg, t);
    } else {
      logger.debug(msg);
    }
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return logger.isDebugEnabled(marker);
  }

  @Override
  public void debug(Marker marker, String msg) {
    logger.debug(marker, msg);

  }

  @Override
  public void debug(Marker marker, String format, Object arg) {
    logger.debug(marker, format, arg);

  }

  @Override
  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    logger.debug(marker, format, arg1, arg2);

  }

  @Override
  public void debug(Marker marker, String format, Object... arguments) {
    logger.debug(marker, format, arguments);

  }

  @Override
  public void debug(Marker marker, String msg, Throwable t) {
    if (isReleaseVersion) {
      logger.debug(marker, msg, t);
    } else {
      logger.debug(marker, msg);
    }
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public void info(String msg) {
    logger.info(msg);

  }

  @Override
  public void info(String format, Object arg) {
    logger.info(format, arg);

  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    logger.info(format, arg1, arg2);

  }

  @Override
  public void info(String format, Object... arguments) {
    logger.info(format, arguments);

  }

  @Override
  public void info(String msg, Throwable t) {
    if (isReleaseVersion) {
      logger.info(msg, t);
    } else {
      logger.info(msg);
    }
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return logger.isInfoEnabled(marker);
  }

  @Override
  public void info(Marker marker, String msg) {
    logger.info(marker, msg);
  }

  @Override
  public void info(Marker marker, String format, Object arg) {
    logger.info(marker, format, arg);
  }

  @Override
  public void info(Marker marker, String format, Object arg1, Object arg2) {
    logger.info(marker, format, arg1, arg2);
  }

  @Override
  public void info(Marker marker, String format, Object... arguments) {
    logger.info(marker, format, arguments);
  }

  @Override
  public void info(Marker marker, String msg, Throwable t) {
    if (isReleaseVersion) {
      logger.info(marker, msg, t);
    } else {
      logger.info(marker, msg);
    }
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  public void warn(String msg) {
    logger.warn(msg);
  }

  @Override
  public void warn(String format, Object arg) {
    logger.warn(format, arg);
  }

  @Override
  public void warn(String format, Object... arguments) {
    logger.warn(format, arguments);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    logger.warn(format, arg1, arg2);
  }

  @Override
  public void warn(String msg, Throwable t) {
    if (isReleaseVersion) {
      logger.warn(msg, t);
    } else {
      logger.warn(msg);
    }
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return logger.isWarnEnabled(marker);
  }

  @Override
  public void warn(Marker marker, String msg) {
    logger.warn(marker, msg);
  }

  @Override
  public void warn(Marker marker, String format, Object arg) {
    logger.warn(marker, format, arg);
  }

  @Override
  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    logger.warn(marker, format, arg1, arg2);
  }

  @Override
  public void warn(Marker marker, String format, Object... arguments) {
    logger.warn(marker, format, arguments);
  }

  @Override
  public void warn(Marker marker, String msg, Throwable t) {
    if (isReleaseVersion) {
      logger.warn(marker, msg, t);
    } else {
      logger.warn(marker, msg);
    }
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public void error(String msg) {
    if (!isReleaseVersion) {
      logger.error(msg);
    }
  }

  @Override
  public void error(String format, Object arg) {
    if (!isReleaseVersion) {
      logger.error(format, arg);
    }
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    if (!isReleaseVersion) {
      logger.error(format, arg1, arg2);
    }
  }

  @Override
  public void error(String format, Object... arguments) {
    if (!isReleaseVersion) {
      logger.error(format, arguments);
    }
  }

  @Override
  public void error(String msg, Throwable t) {
    if (!isReleaseVersion) {
      logger.error(msg, t);
    }
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return logger.isErrorEnabled(marker);
  }

  @Override
  public void error(Marker marker, String msg) {
    if (!isReleaseVersion) {
      logger.error(marker, msg);
    }
  }

  @Override
  public void error(Marker marker, String format, Object arg) {
    if (!isReleaseVersion) {
      logger.error(marker, format, arg);
    }
  }

  @Override
  public void error(Marker marker, String format, Object arg1, Object arg2) {
    if (!isReleaseVersion) {
      logger.error(marker, format, arg1, arg2);
    }
  }

  @Override
  public void error(Marker marker, String format, Object... arguments) {
    if (!isReleaseVersion) {
      logger.error(marker, format, arguments);
    }
  }

  @Override
  public void error(Marker marker, String msg, Throwable t) {
    if (!isReleaseVersion) {
      logger.error(marker, msg, t);
    }
  }
}
