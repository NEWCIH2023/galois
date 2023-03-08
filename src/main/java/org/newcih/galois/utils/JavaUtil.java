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

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;

import static org.newcih.galois.constants.FileTypeConstant.CLASS_FILE;

public class JavaUtil {

    private static final GaloisLog logger = GaloisLog.getLogger(JavaUtil.class);
    private static final String compileDir;
    private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private static Instrumentation inst;

    static {
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
    }

    public static void compile(File file) {
        compiler.run(null, null, null, "-d", compileDir, file.getAbsolutePath());
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

        StringBuilder classFilePath = new StringBuilder(compileDir);
        for (String subPath : className.split("\\.")) {
            classFilePath.append(subPath).append(File.separator);
        }
        classFilePath.append(CLASS_FILE);

        return new File(classFilePath.toString());
    }

    public static Instrumentation getInst() {
        return inst;
    }

    public static void setInst(Instrumentation inst) {
        JavaUtil.inst = inst;
    }

    public static void main(String[] args) throws IOException {
        File tmpFile = new File("C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\src\\main\\java\\org\\newcih" +
                "\\galois\\utils\\JavaUtil.java");
        compile(tmpFile);
    }
}
