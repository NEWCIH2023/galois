/*
 * MIT License
 *
 * Copyright (c) [2023] [$user]
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

package io.liuguangsheng.galois.conf;

import io.liuguangsheng.galois.constants.Constant;
import io.liuguangsheng.galois.utils.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * global configuration service
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class GlobalConfiguration {

  private static final GlobalConfiguration globalConfiguration = new GlobalConfiguration();
  public static final String GALOIS_PROPERTIES = "galois.properties";
  /**
   * parse config key-value entry in galois.properties
   */
  private final Properties configuration = new Properties();

  private GlobalConfiguration() {
    try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(GALOIS_PROPERTIES)) {
      configuration.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * get instance
   *
   * @return {@link GlobalConfiguration}
   * @see GlobalConfiguration
   */
  public static GlobalConfiguration getInstance() {
    return globalConfiguration;
  }

  /**
   * get string
   *
   * @param key key
   * @return {@link String}
   * @see String
   */
  public String getString(String key) {
    return getString(key, Constant.EMPTY);
  }

  /**
   * get string
   *
   * @param key          key
   * @param defaultValue defaultValue
   * @return {@link String}
   * @see String
   */
  public String getString(String key, String defaultValue) {
    if (StringUtil.isBlank(key)) {
      return defaultValue;
    }

    String result = configuration.getProperty(key);

    if (StringUtil.isBlank(result)) {
      return System.getProperty(key, defaultValue);
    }

    return result;
  }

  /**
   * get boolean
   *
   * @param key key
   * @return {@link boolean}
   */
  public boolean getBoolean(String key) {
    return getBoolean(key, false);
  }

  /**
   * get boolean
   *
   * @param key          key
   * @param defaultValue defaultValue
   * @return {@link boolean}
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    String result = getString(key, defaultValue ? Constant.TRUE : Constant.FALSE);
    return Constant.TRUE.equalsIgnoreCase(result);
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
    String result = getString(key, String.valueOf(defaultValue));
    try {
      return Long.parseLong(result);
    } catch (Exception e) {
      return -1L;
    }
  }

  /**
   * get integer
   *
   * @param key key
   * @return {@link int}
   */
  public int getInteger(String key) {
    return getInteger(key, 0);
  }

  /**
   * get integer
   *
   * @param key          key
   * @param defaultValue defaultValue
   * @return {@link int}
   */
  public int getInteger(String key, int defaultValue) {
    String result = getString(key, String.valueOf(defaultValue));
    try {
      return Integer.parseInt(result);
    } catch (Exception e) {
      return -1;
    }
  }

}
