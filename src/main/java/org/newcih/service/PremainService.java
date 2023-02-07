package org.newcih.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class PremainService {

    public static final Logger LOGGER = LoggerFactory.getLogger(PremainService.class);
    public static final String CLASS_PATH = "C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\target\\classes";

    public static ReloadClassLoader CLASS_LOADER = new ReloadClassLoader(CLASS_PATH);

    public static void premain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException, ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("premain was called");
        }

        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classFileBuffer) -> {
            if (className.contains("CopyClassExample")) {
                LOGGER.info("已经加载CopyClassExample");
            }

            if (LOGGER.isDebugEnabled()) {
                if (loader != null) {
                    LOGGER.debug("使用 {} 加载 {}", loader, className);
                }
            }

            return classFileBuffer;
        });

        final AtomicBoolean invokeMethodFlag = new AtomicBoolean(false);
        AsmDemo asmDemo = new AsmDemo();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    if (invokeMethodFlag.get()) {
                        asmDemo.createClassWithTestMethod();
                        LOGGER.info("had replace class file with test method");
                    } else {
                        asmDemo.createClassWithoutTestMethod();
                        LOGGER.info("had replace class file without test method");
                    }

                    invokeMethodFlag.set(!invokeMethodFlag.get());
                } catch (Exception e) {
                    LOGGER.error("替换class文件失败", e);
                }
            }
        }, 5_000L, 10_000L);

        try {
            FileWatchService fileWatchService = new FileWatchService(new String[]{"C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\target\\classes"});
            fileWatchService.setConsumers(eventList -> {

                eventList.forEach(event -> {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("premain watching {}, [{}]", event.context(), event.kind());
                    }
                });

                try {
                    CLASS_LOADER = new ReloadClassLoader(CLASS_PATH);

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("my loader will load class CopyClassExample, with parent is {} ", CLASS_LOADER.getParent());
                    }

                    Class<?> copyClassExample = CLASS_LOADER.loadClass("CopyClassExample");

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("my loader loaded class CopyClassExample ");
                    }

                    Object obj = copyClassExample.newInstance();
                    Method method = Arrays.stream(obj.getClass().getMethods()).filter(m -> m.getName().equals("test")).findFirst().orElseThrow(() -> new NullPointerException("找不到test方法"));
                    method.invoke(obj);
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException | NullPointerException e) {
                    LOGGER.error("load CopyClassExample class or invoke method error", e);
                }
            });

            fileWatchService.action();
        } catch (IOException e) {
            LOGGER.error("fileWatchService error", e);
        }

    }

}
