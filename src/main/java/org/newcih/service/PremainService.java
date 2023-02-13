package org.newcih.service;

import org.newcih.util.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

public class PremainService {

    public static final Logger LOGGER = LoggerFactory.getLogger(PremainService.class);

    public static final String GRADLE_OUTPUT = "build";

    public static final String MAVEN_OUTPUT = "target";

    public static final boolean useMaven = false;

    public static void premain(String agentArgs, Instrumentation inst) {
        LOGGER.info("premain was called");

        int loadedClassLength = inst.getAllLoadedClasses().length;
        Class<?> clazz = inst.getAllLoadedClasses()[loadedClassLength - 1];
        String classPath = Objects.requireNonNull(clazz.getResource("")).getPath();
        String outputPath;

        if (useMaven) {
            classPath = classPath.substring(1).replace("/", "\\");
            outputPath = String.format("%s%s\\classes\\", classPath, GRADLE_OUTPUT);
        } else {
            outputPath = classPath;
        }

        ApacheFileWatchService targetWatch = new ApacheFileWatchService(outputPath);
        targetWatch.setIncludeFileTypes(Collections.singletonList("class"));

        Consumer<File> handler = file -> {
            String path = file.getAbsolutePath();
            String className = path.replace(outputPath, "").replaceAll(SystemUtils.isWindowOS() ? "\\\\" : "/", ".").replace(".class", "");

            try {
                ReloadClassLoader reloadClassLoader = new ReloadClassLoader(Collections.singletonList(outputPath));
                reloadClassLoader.loadClass(className);
                LOGGER.info("使用 {} 加载 {}", reloadClassLoader, className);
            } catch (Throwable e) {
                LOGGER.error("reload class file throw exception", e);
            }
        };

        targetWatch.setModiferHandler(handler);
        targetWatch.setCreateHandler(handler);
        targetWatch.start();
    }

}
