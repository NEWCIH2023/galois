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

package org.newcih.galois.conf;

import static org.newcih.galois.constants.Constant.EMPTY;
import static org.newcih.galois.constants.Constant.FALSE;
import static org.newcih.galois.constants.Constant.TRUE;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.newcih.galois.utils.FileUtil;
import org.newcih.galois.utils.StringUtil;

/**
 * global configuration service
 */
public class GlobalConfiguration {

  /**
   * parse config key-value entry in galois.properties
   */
  private static final Properties configuration = new Properties();
  private static final GlobalConfiguration globalConfiguration = new GlobalConfiguration();

  static {
    try (InputStream is = FileUtil.readClassPathFile("galois.properties")) {
      configuration.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private GlobalConfiguration() {
  }

  public static GlobalConfiguration getInstance() {
    return globalConfiguration;
  }

  public String getString(String key) {
    return getString(key, EMPTY);
  }

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

  public boolean getBoolean(String key) {
    return getBoolean(key, false);
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    String result = getString(key, defaultValue ? TRUE : FALSE);
    return TRUE.equalsIgnoreCase(result);
  }

  public long getLong(String key) {
    return getLong(key, 0L);
  }

  public long getLong(String key, long defaultValue) {
    String result = getString(key, String.valueOf(defaultValue));
    try {
      return Long.parseLong(result);
    } catch (Exception e) {
      return -1L;
    }
  }

  public int getInteger(String key) {
    return getInteger(key, 0);
  }

  public int getInteger(String key, int defaultValue) {
    String result = getString(key, String.valueOf(defaultValue));
    try {
      return Integer.parseInt(result);
    } catch (Exception e) {
      return -1;
    }
  }

}
