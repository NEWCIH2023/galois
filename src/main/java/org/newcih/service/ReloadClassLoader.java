package org.newcih.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReloadClassLoader extends ClassLoader {

    public String classpath;

    public static final Logger LOGGER = LoggerFactory.getLogger(ReloadClassLoader.class);

    public static final String[] JVM_PACKAGES = new String[]{"java", "javax", "org.omg", "org.ietf", "org.w3c", "org.xml", "jdk"};

    public ReloadClassLoader(String classpath) {
        this.classpath = classpath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("will use findClass method in ReloadClassLoader to find class {}", name);
        }

        String path = name.replaceAll("\\.", "\\\\");

        try {
            byte[] classBytes = getClassBytes(classpath + "\\" + path + ".class");
            return defineClass(name, classBytes, 0, Objects.requireNonNull(classBytes).length);
        } catch (Exception e) {
            LOGGER.error("寻找类{}发生异常", name, e);
        }

        return null;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        for (String jvmPackage : JVM_PACKAGES) {
            if (name.startsWith(jvmPackage)) {
                return super.loadClass(name, resolve);
            }
        }

        synchronized (getClassLoadingLock(name)) {
            // If still not found, then invoke findClass in order
            // to find the class.
            long t1 = System.nanoTime();
            Class<?> c = findClass(name);

            // this is the defining class loader; record the stats
            sun.misc.PerfCounter.getParentDelegationTime().addTime(0);
            sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
            sun.misc.PerfCounter.getFindClasses().increment();

            if (resolve) {
                resolveClass(c);
            }

            return c;
        }
    }

    private static byte[] getClassBytes(String path) {
        try (InputStream is = Files.newInputStream(Paths.get(path)); ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            LOGGER.error("getClassBytes方法失败", e);
        }

        return null;
    }
}
