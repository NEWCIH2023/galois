package org.newcih.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

public class PremainService {

    public static final Logger LOGGER = LoggerFactory.getLogger(PremainService.class);

    public static void premain(String agentArgs, Instrumentation inst) {
        LOGGER.info("premain was called");

        int loadedClassLength = inst.getAllLoadedClasses().length;
        Class<?> clazz = inst.getAllLoadedClasses()[loadedClassLength - 1];
        String classPath = Objects.requireNonNull(clazz.getResource("")).getPath();

        classPath = classPath.substring(1).replace("/", "\\");
        String outputPath = String.format("%starget\\classes\\", classPath);
        System.out.println(outputPath);
        ApacheFileWatchService targetWatch = new ApacheFileWatchService(outputPath);
        targetWatch.setIncludeFileTypes(Collections.singletonList("class"));

        Consumer<File> handler = file -> {
            String path = file.getAbsolutePath();
            System.out.println("path is " + path);
            System.out.println("outputpath is " + outputPath);
            String className = path.replace(outputPath, "").replaceAll("\\\\", ".").replace(".class", "");
            System.out.println("className is " + className);

            try {
                ReloadClassLoader reloadClassLoader = new ReloadClassLoader(Collections.singletonList(outputPath));
                reloadClassLoader.loadClass(className);
            } catch (Exception e) {
                LOGGER.error("reload class file throw exception", e);
            }
        };

        targetWatch.setModiferHandler(handler);
        targetWatch.setCreateHandler(handler);
        targetWatch.start();
    }

}
