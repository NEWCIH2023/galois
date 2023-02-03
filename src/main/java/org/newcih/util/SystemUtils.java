package org.newcih.util;

public class SystemUtils {

    /**
     * 是否是Windows操作系统
     *
     * @return
     */
    public static boolean isWindowOS() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
