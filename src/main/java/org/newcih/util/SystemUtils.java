package org.newcih.util;

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
}
