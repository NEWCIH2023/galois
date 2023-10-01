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

package io.liuguangsheng.galois.service.spring;

import io.liuguangsheng.galois.conf.GlobalConfiguration;
import io.liuguangsheng.galois.service.spring.visitors.SpringBootBannerVisitor;
import io.liuguangsheng.galois.utils.StringUtil;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static io.liuguangsheng.galois.constants.ConfConstant.*;
import static io.liuguangsheng.galois.constants.Constant.*;

/**
 * print banner when galois starting
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class BannerService implements SpringBootBannerVisitor.NecessaryMethods {

    private static final GlobalConfiguration config = GlobalConfiguration.getInstance();

    /**
     * print banner
     */
    @Override
    public void printBanner() {
        if (!config.getBool(BANNER_ENABLE, true)) {
            return;
        }

        String galoisName = " :: Galois :: ";
        String gitUrl = "";
        String galoisGitUrl = config.getStr(GALOIS_GIT_URL);
        if (StringUtil.isNotBlank(galoisGitUrl)) {
            gitUrl += String.join(" | ", galoisGitUrl.split(COMMA));
        }
        System.out.println(AnsiOutput.toString(AnsiColor.GREEN, galoisName,
                AnsiColor.DEFAULT, SPACE, AnsiStyle.FAINT, galoisVersion() + SPACE + gitUrl));
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
            Field knownMappers = mapperRegistry.getDeclaredField(KNOWN_MAPPERS);
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
        return String.format("%s", config.getStr(GALOIS_VERSION, HYPHEN));
    }
}
