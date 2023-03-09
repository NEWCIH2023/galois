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

package org.newcih.galois.utils;

import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.instrument.Instrumentation;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.newcih.galois.constants.Constant.*;
import static org.newcih.galois.constants.FileTypeConstant.CLASS_FILE;

public class JavaUtil {

    private static final GaloisLog logger = GaloisLog.getLogger(JavaUtil.class);
    private static final String compileDir;
    private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    public static Instrumentation inst;
    private static final Context context = new Context();
    private static final JavacFileManager manager;
    private static final JavacTool javacTool = JavacTool.create();
    private static final Pattern packagePattern = Pattern.compile("^package +(\\S+);");
    private static final Pattern classNamePattern = Pattern.compile("[\\s\\S]*class +(\\S+) +[\\s\\S]*");

    static {
        manager = new JavacFileManager(context, true, Charset.defaultCharset());

        compileDir = System.getProperty("java.io.tmpdir") + File.separator + "GaloisCompile" + File.separator;
        File directory = new File(compileDir);
        if (!directory.exists()) {
            try {
                boolean createResult = directory.mkdir();
                if (!createResult) {
                    logger.error("can't create temp compile directory");
                }
            } catch (Exception e) {
                logger.error("create temp compile directory failed", e);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("using tmp directory ==> {}", compileDir);
        }
    }

    public static File compileSource(File sourceFile) {
        String className = getClassNameFromSource(sourceFile);
        compiler.run(null, null, null, "-d", compileDir, sourceFile.getAbsolutePath());
        return getClassFile(className);
    }

    public static Instrumentation getInst() {
        return inst;
    }

    public static String getClassNameFromSource(File javaFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(javaFile))) {
            String tmp = "";
            String result = "";

            while ((tmp = br.readLine()) != null) {
                if (tmp.startsWith(PACKAGE + SPACE)) {
                    Matcher packageMatcher = packagePattern.matcher(tmp);
                    if (packageMatcher.matches()) {
                        result = packageMatcher.group(1);
                    } else {
                        logger.warn("can't get package using pattern from java source {}", javaFile);
                        return null;
                    }
                }

                if (tmp.contains(CLASS + SPACE)) {
                    Matcher classNameMatcher = classNamePattern.matcher(tmp);
                    if (classNameMatcher.matches()) {
                        result += DOT + classNameMatcher.group(1);
                    } else {
                        logger.warn("can't get className using pattern from java source {}", javaFile);
                        return null;
                    }

                    break;
                }
            }

            return result;
        } catch (Exception e) {
            logger.error("parse className from java source failed", e);
            return null;
        }
    }

    public static File getClassFile(Class<?> clazz) {
        return getClassFile(clazz.getName());
    }

    /**
     * @param className must include package path
     * @return
     */
    public static File getClassFile(String className) {
        if (StringUtil.isBlank(className)) {
            return null;
        }

        return new File(compileDir + String.join(File.separator, className.split("\\.")) + CLASS_FILE);
    }

}
