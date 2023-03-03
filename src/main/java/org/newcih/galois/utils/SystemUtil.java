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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class SystemUtil {

    public static final String OS_NAME = "os.name";
    public static final String WIN = "Windows";

    private static final String CLASSES_PATH = "classes";

    /**
     * 是否是Windows操作系统
     *
     * @return
     */
    public static boolean isWindowOS() {
        return System.getProperty(OS_NAME).startsWith(WIN);
    }

    /**
     * 将File转为byte[]数组
     *
     * @param file
     * @return
     */
    public static byte[] readFile(File file) {
        if (file == null) {
            return null;
        }

        System.out.println("before new fis, file is " + file);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] b = new byte[1024];
            int n;
            while ((n = fileInputStream.read(b)) != -1) {
                byteArrayOutputStream.write(b, 0, n);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            System.out.println("current file is " + file);
            e.printStackTrace();
        }

        return new byte[0];
    }

    /**
     * 从class文件获取className
     *
     * @param classFile
     * @return
     */
    public static String getClassName(String prefixPath, File classFile) {
        return classFile.getAbsolutePath()
                .replace(prefixPath, "")
                .replaceAll(SystemUtil.isWindowOS() ? "\\\\" : "/", ".")
                .replace(".class", "");
    }
}
