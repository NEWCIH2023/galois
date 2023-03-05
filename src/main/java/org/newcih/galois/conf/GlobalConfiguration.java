/*
 * MIT License
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
