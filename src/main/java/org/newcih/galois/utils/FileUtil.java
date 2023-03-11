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

import org.newcih.galois.constants.FileType;

import java.io.*;
import java.util.Objects;

import static org.newcih.galois.constants.Constant.LF;

public class FileUtil {

    private FileUtil() {
    }

    private static final GaloisLog logger = GaloisLog.getLogger(FileUtil.class);

    public static boolean validFileType(File file, FileType fileType) {
        return Objects.equals(getFileType(file), fileType.getFileType());
    }

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
     *
     * @param file
     * @return
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
        } catch (Exception e) {
            logger.error("convert file to byte[] failed", e);
        }

        return new byte[0];
    }

    public static String readTextFile(File file) {
        if (file == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.lines().map(line -> line + LF).forEach(sb::append);
        } catch (Exception e) {
            logger.error("readTextFile throw exception", e);
            return "";
        }

        return sb.toString();
    }

}
