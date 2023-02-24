package org.newcih.service.agent;

import org.newcih.service.agent.frame.mybatis.MyBatisTransformer;
import org.newcih.service.agent.frame.spring.SpringTransformer;
import org.newcih.service.watch.ApacheFileWatchService;
import org.newcih.service.watch.frame.FileChangedListener;
import org.newcih.service.watch.frame.mybatis.MyBatisXmlListener;
import org.newcih.service.watch.frame.spring.SpringBeanListener;
import org.newcih.util.GaloisLog;
import org.newcih.util.SystemUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * PreMain服务类
 */
public class PremainService {

    public static final GaloisLog LOGGER = GaloisLog.getLogger(PremainService.class);

    /**
     * Premain入口
     *
     * @param agentArgs
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        LOGGER.info("PremainService服务启动");

        inst.addTransformer(new SpringTransformer());
        inst.addTransformer(new MyBatisTransformer());

        List<FileChangedListener> fileChangedListeners = Arrays.asList(new SpringBeanListener(), new MyBatisXmlListener());
        String outputPath = SystemUtils.getOutputPath();
        ApacheFileWatchService watchService = new ApacheFileWatchService(outputPath);

        BiConsumer<File, Consumer<FileChangedListener>> commonHandler = (file, handler) -> fileChangedListeners.stream()
                .filter(listener -> listener.validFile(file))
                .forEach(handler);

        watchService.setCreateHandler(file -> commonHandler.accept(file, listener -> listener.fileCreatedHandle(file, inst)));
        watchService.setModiferHandler(file -> commonHandler.accept(file, listener -> listener.fileModifiedHandle(file, inst)));
        watchService.setDeleteHandler(file -> commonHandler.accept(file, listener -> listener.fileDeletedHandle(file, inst)));

        watchService.start();
    }

    /**
     * Premain入口
     *
     * @param agentArgs
     * @param inst
     */
    @Deprecated
    public static void oldpremain(String agentArgs, Instrumentation inst) {
        LOGGER.info("PremainService服务启动");

        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            AgentService.attachProcess(pid);
        } catch (Exception e) {
            LOGGER.error("启动AgentService服务失败", e);
            throw new RuntimeException(e);
        }

        inst.addTransformer(new SpringTransformer(), true);
        inst.addTransformer(new MyBatisTransformer(), true);

        LOGGER.info("Premain服务执行完毕");
    }

}
