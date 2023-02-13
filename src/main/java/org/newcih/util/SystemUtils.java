package org.newcih.util;

public class SystemUtils {

    public static final String OS_NAME = "os.name";

    public static Boolean IS_WINDOW = null;
    public static final String WIN = "Windows";

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
}
