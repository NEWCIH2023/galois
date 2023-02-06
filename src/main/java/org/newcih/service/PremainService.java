package org.newcih.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class PremainService {

    public static final Logger LOGGER = LoggerFactory.getLogger(PremainService.class);
    public static final String BASE_PACKAGE = "org/newcih";
    public static final String BASE_PATH = "org\\newcih";
    public static final String CLASS_FILE_TYPE = ".class";

    public static final ReloadClassLoader CLASS_LOADER = new ReloadClassLoader();

    public static void premain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException {
        System.out.println("premain was called");

        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classFileBuffer) -> {
            if (loader != null) {
                loader = CLASS_LOADER;
                System.out.printf("加载类 %s 所用类加载器 %s 的cp路径 %s \n", className, loader, Objects.requireNonNull(loader.getResource("")).getPath());
                try {
                    CLASS_LOADER.loadClass(className);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                return classFileBuffer;
            } else return classFileBuffer;
        });

        try {
            FileWatchService fileWatchService = new FileWatchService(new String[]{"C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\target\\classes"});
            fileWatchService.setConsumer(watchEvent -> {
                System.out.println("premain watching " + watchEvent.context() + " - " + watchEvent.kind());
                System.out.printf("%s加载的类%s\n", CLASS_LOADER, Arrays.stream(CLASS_LOADER.getPackages()).map(Package::getName).collect(Collectors.joining(";")));
            });

            fileWatchService.action();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
