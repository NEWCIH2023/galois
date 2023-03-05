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

package org.newcih.galois.service;

import java.lang.reflect.Method;

public class BannerService {

    public static final String BANNER =
            "  ____       _       _     \n" +
                    " / ___| __ _| | ___ (_)___ \n" +
                    "| |  _ / _` | |/ _ \\| / __|\n" +
                    "| |_| | (_| | | (_) | \\__ \\\n" +
                    " \\____|\\__,_|_|\\___/|_|___/";

    private BannerService() {
    }

    public static void printBanner() {
        System.out.println(BANNER);
        System.out.printf(" :: SpringBoot :: (%s) :: Spring :: (%s) :: MyBatis :: (%s) ::%n%n",
                springBootVersion(), springVersion(), mybatisVersion());
    }

    public static String springBootVersion() {
        try {
            Class<?> springBootVersion = Class.forName("org.springframework.boot.SpringBootVersion");
            Method getVersion = springBootVersion.getDeclaredMethod("getVersion");
            return (String) getVersion.invoke(null);
        } catch (Exception e) {
            return "-";
        }
    }

    public static String springVersion() {
        try {
            Class<?> springVersion = Class.forName("org.springframework.core.SpringVersion");
            Method getVersion = springVersion.getDeclaredMethod("getVersion");
            return (String) getVersion.invoke(null);
        } catch (Exception e) {
            return "-";
        }
    }

    public static String mybatisVersion() {
        return "-";
    }
}
