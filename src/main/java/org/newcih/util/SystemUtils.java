package org.newcih.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Objects;

public class SystemUtils {

    public static final String OS_NAME = "os.name";
    public static final String WIN = "Windows";
    public static Boolean IS_WINDOW = null;

    /**
     * 是否是Windows操作系统
     *
     * @return
     */
    public static boolean isWindowOS() {
        if (IS_WINDOW != null) {
            return IS_WINDOW;
        }

        IS_WINDOW = System.getProperty(OS_NAME).startsWith(WIN);
        return IS_WINDOW;
    }

    public static String getOutputPath(final Instrumentation inst, final boolean useMaven) {
        Class<?>[] classes = inst.getAllLoadedClasses();
        int length = classes.length;
        Class<?> clazz = classes[length - 1];
        String classPath = Objects.requireNonNull(clazz.getResource("")).getPath();

        if (useMaven) {
            classPath = classPath.substring(1).replace("/", "\\");
        }

        return classPath;
    }

    public static byte[] readFile(File file) {
        if (file == null) {
            return null;
        }
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fileInputStream.read(b)) != -1) {
                byteArrayOutputStream.write(b, 0, n);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
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
