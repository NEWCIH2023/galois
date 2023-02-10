package org.newcih.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Arrays;
import java.util.function.Consumer;

public class PremainService {

    public static final Logger LOGGER = LoggerFactory.getLogger(PremainService.class);
    public static final String CLASS_PATH = "C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\target\\classes";

    public static final String CLASS_FILE_TYPE = ".class";
    public static ReloadClassLoader reloadClassLoader = new ReloadClassLoader(Arrays.asList(CLASS_PATH));

    public static final String CLASS_PATH_PATTERN = "C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\target\\classes\\";

    public static void premain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException, ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException {
        LOGGER.info("premain was called");

        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classFileBuffer) -> {
            if (LOGGER.isDebugEnabled()) {
                if (loader != null && !loader.toString().contains("AppClassLoader")) {
                    LOGGER.debug("使用 {} 加载 {}", loader, className);
                }
            }

            return classFileBuffer;
        });

        ApacheFileWatchService targetWatch = new ApacheFileWatchService("C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\target\\classes");

        Consumer<File> handler = file -> {

            if (file.isDirectory()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("directory {} is changing, ignore that...", file.getName());
                }
                return;
            }

            String path = file.getAbsolutePath();
            String className = path.replace(CLASS_PATH_PATTERN, "").replaceAll("\\\\", ".").replace(".class", "");

            try {
                reloadClassLoader.clearAssertionStatus();
                reloadClassLoader = new ReloadClassLoader(Arrays.asList(CLASS_PATH));
                Class<?> copyClassExample = reloadClassLoader.loadClass(className);
                Object obj = copyClassExample.newInstance();
                Arrays.stream(obj.getClass().getMethods())
                        .filter(m -> m.getName().equals("test"))
                        .findFirst()
                        .orElseThrow(() -> new NullPointerException(className + "找不到 test 方法"))
                        .invoke(obj);
            } catch (Exception e) {
                LOGGER.error("load CopyClassExample class or invoke method error", e);
            }
        };

        targetWatch.setModiferHandler(handler);
        targetWatch.setCreateHandler(handler);

        targetWatch.start();
    }

}
