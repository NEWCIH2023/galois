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

package org.newcih.galois.utils;

import org.newcih.galois.constants.FileType;

import java.io.*;
import java.util.Objects;

import static org.newcih.galois.constants.Constant.LF;

public class FileUtil {

    private FileUtil() {
    }

    /**
     * 验证文件类型是否匹配
     */
    public static boolean validFileType(File file, FileType fileType) {
        return Objects.equals(getFileType(file), fileType.getFileType());
    }

    /**
     * 获取文件类型的通用方法，带上.字符
     */
    public static String getFileType(File file) {
        if (file == null) {
            return "";
        }

        String name = file.getName();
        int index = name.lastIndexOf(".");

        if (index != -1) {
            String fileType = name.substring(index);
            return fileType.toLowerCase();
        }

        return "";
    }

    /**
     * 将字节数组写入对应路径的文件
     */
    public static File writeFile(byte[] bytes, String path) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new File(path);
    }

    /**
     * 将File转为byte[]数组
     */
    public static byte[] readFile(File file) {
        if (file == null) {
            return new byte[0];
        }

        try (FileInputStream fileInputStream = new FileInputStream(file);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] b = new byte[1024];
            int n;
            while ((n = fileInputStream.read(b)) != -1) {
                byteArrayOutputStream.write(b, 0, n);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (Exception ignored) {
        }

        return new byte[0];
    }

    /**
     * 读取字符类型文件，并返回String类型结果
     */
    public static String readTextFile(File file) {
        if (file == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.lines().map(line -> line + LF).forEach(sb::append);
        } catch (Exception e) {
            return "";
        }

        return sb.toString();
    }

    /**
     * 读取classpath路径下的文件
     *
     * @param relativePath 文件的相对路径，相对于classpath路径
     */
    public static InputStream readClassPathFile(String relativePath) {
        if (StringUtil.isBlank(relativePath)) {
            return null;
        }

        return FileUtil.class.getClassLoader().getResourceAsStream(relativePath);
    }
}
