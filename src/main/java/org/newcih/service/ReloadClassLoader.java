package org.newcih.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReloadClassLoader extends ClassLoader {

    private final List<String> classpaths = Lists.newArrayListWithCapacity(10);

    public static final Logger LOGGER = LoggerFactory.getLogger(ReloadClassLoader.class);

    public static final String[] IGNORE_PACKAGE = new String[]{"java", "javax", "org.omg", "org.ietf", "org.w3c", "org.xml", "jdk", "org.objectweb"};

    public ReloadClassLoader(List<String> classpaths) {
        this.classpaths.addAll(classpaths);
    }

    @Override
    protected Class<?> findClass(String name) {
        String path = name.replaceAll("\\.", "\\\\");

        try {
            byte[] classBytes = getClassBytes(path + ".class");
            return defineClass(name, classBytes, 0, Objects.requireNonNull(classBytes).length);
        } catch (Exception e) {
            LOGGER.error("寻找类{}发生异常", name, e);
        }

        return null;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        for (String jvmPackage : IGNORE_PACKAGE) {
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

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, true);
    }

    private byte[] getClassBytes(String path) {
        String fullPath = "";

        for (String classpath : classpaths) {
            if (!Strings.isNullOrEmpty(fullPath = Paths.get(classpath + "\\" + path).toString())) {
                break;
            }
        }

        try (InputStream is = Files.newInputStream(Paths.get(fullPath)); ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
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

    public void addClassPath(List<String> classPaths) {
        this.classpaths.addAll(classPaths);
    }

    public void removeClassPath(List<String> classpaths) {
        this.classpaths.removeAll(classpaths);
    }
}
