package org.newcih.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

public class SystemUtils {

    public static final String OS_NAME = "os.name";
    public static final String WIN = "Windows";

    /**
     * 是否是Windows操作系统
     *
     * @return
     */
    public static boolean isWindowOS() {
        return System.getProperty(OS_NAME).startsWith(WIN);
    }

    /**
     * 获取项目编译输出路径
     *
     * @return
     */
    public static String getOutputPath() {
        return getOutputPath(null);
    }

    /**
     * 获取项目编译输出路径
     *
     * @param clazz 通过这个类获取classpath路径
     * @return
     */
    public static String getOutputPath(Class<?> clazz) {
        if (clazz == null) {
            clazz = String.class;
        }

        String originClassPath = Objects.requireNonNull(clazz.getResource("/")).getPath();
        if (isWindowOS()) {
            return originClassPath.substring(1).replace("/", File.separator);
        }

        return originClassPath;
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

        try (
                FileInputStream fileInputStream = new FileInputStream(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ) {
            byte[] b = new byte[1024];
            int n;
            while ((n = fileInputStream.read(b)) != -1) {
                byteArrayOutputStream.write(b, 0, n);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
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
        return classFile.getAbsolutePath().replace(prefixPath, "").replaceAll(SystemUtils.isWindowOS() ? "\\\\" : "/", ".").replace(".class", "");
    }
}
