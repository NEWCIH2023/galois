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

package org.newcih.galois.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import org.newcih.galois.conf.GlobalConfiguration;

import static org.newcih.galois.constants.ConfConstant.BANNER_ENABLE;
import static org.newcih.galois.constants.ConfConstant.BUILD_TYPE;
import static org.newcih.galois.constants.ConfConstant.GALOIS_VERSION;
import static org.newcih.galois.constants.Constant.LF;

/**
 * print banner when galois starting
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class BannerService {

    private static final GlobalConfiguration globalConfig = GlobalConfiguration.getInstance();
    private static final String BANNER =
            "  ____       _       _     \n" +
                    " / ___| __ _| | ___ (_)___ \n" +
                    "| |  _ / _` | |/ _ \\| / __|\n" +
                    "| |_| | (_| | | (_) | \\__ \\\n" +
                    " \\____|\\__,_|_|\\___/|_|___/\t\t(" + galoisVersion() + ")";

    private BannerService() {
    }

    /**
     * print banner
     */
    public static void printBanner() {
        if (!globalConfig.getBoolean(BANNER_ENABLE, true)) {
            return;
        }

        String bannerBuilder = BANNER + LF +
                String.format(
                        " :: SpringBoot (%s) :: Spring (%s) :: MyBatis (%s)%n :: Jdk (%s)",
                        springBootVersion(), springVersion(), mybatisVersion(), jdkVersion()
                ) +
                LF;
        System.out.println(bannerBuilder);
    }

    /**
     * spring boot version
     *
     * @return {@link String}
     * @see String
     */
    private static String springBootVersion() {
        try {
            Class<?> springBootVersion = Class.forName("org.springframework.boot.SpringBootVersion");
            Method getVersion = springBootVersion.getDeclaredMethod("getVersion");
            return (String) getVersion.invoke(null);
        } catch (Exception e) {
            return "-";
        }
    }

    /**
     * spring version
     *
     * @return {@link String}
     * @see String
     */
    private static String springVersion() {
        try {
            Class<?> springVersion = Class.forName("org.springframework.core.SpringVersion");
            Method getVersion = springVersion.getDeclaredMethod("getVersion");
            return (String) getVersion.invoke(null);
        } catch (Exception e) {
            return "-";
        }
    }

    /**
     * jdk version
     *
     * @return {@link String}
     * @see String
     */
    private static String jdkVersion() {
        try {
            return System.getProperty("java.version") + " " + System.getProperty("java.vm.name");
        } catch (Exception e) {
            return "-";
        }
    }

    /**
     * mybatis version
     *
     * @return {@link String}
     * @see String
     */
    private static String mybatisVersion() {
        try {
            Class<?> mapperRegistry = Class.forName("org.apache.ibatis.binding.MapperRegistry");
            Field knownMappers = mapperRegistry.getDeclaredField("knownMappers");
            boolean flag = knownMappers.getType().equals(Map.class);
            return flag ? ">= 3.2.0" : "<= 3.1.0";
        } catch (Exception e) {
            return "-";
        }
    }

    /**
     * galois version
     *
     * @return {@link String}
     * @see String
     */
    private static String galoisVersion() {
        return globalConfig.getString(GALOIS_VERSION, "-") + "_" + globalConfig.getString(BUILD_TYPE);
    }
}
