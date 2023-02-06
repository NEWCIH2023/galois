package org.newcih.service;

import com.sun.nio.file.ExtendedWatchEventModifier;
import org.newcih.util.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * 文件变动监听服务
 *
 * @author liuguangsheng
 */
public class FileWatchService {

    public static final Logger LOGGER = LoggerFactory.getLogger(FileWatchService.class);

    /**
     * 监听服务对象
     */
    private final WatchService watchService;

    /**
     * 监听变化后的动作
     */
    private Consumer<List<WatchEvent<?>>> consumers;

    private Consumer<WatchEvent> consumer;

    public FileWatchService(String[] paths) throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
        for (String path : paths) {
            Path tempPath = Paths.get(path);

            // macOS平台JDK不支持Modifer参数
            if (SystemUtils.isWindowOS()) {
                tempPath.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY}, ExtendedWatchEventModifier.FILE_TREE);
            } else {
                tempPath.register(watchService, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE, OVERFLOW);
            }
        }
    }

    /**
     * 启动方法
     */
    public void action() {
        addShutdownHook();
        registerWatchService();
    }

    /**
     * 注册watchService关闭hook
     */
    private void addShutdownHook() {
        Thread shutdownHook = new Thread(() -> {
            try {
                watchService.close();
            } catch (IOException e) {
                throw new RuntimeException("关闭watchService服务发生异常", e);
            }
        });

        Runtime.getRuntime().addShutdownHook(shutdownHook);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FileWatchService注册了ShutdownHook");
        }
    }

    /**
     * 注册watchService服务
     */
    private void registerWatchService() {
        Thread watchDaemon = new Thread(() -> {
            while (true) {
                try {
                    WatchKey watchKey = watchService.take();
                    List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                    if (consumer != null) {
                        watchEvents.forEach(watchEvent -> consumer.accept(watchEvent));
                    } else if (consumers != null) {
                        consumers.accept(watchEvents);
                    }

                    watchKey.reset();
                } catch (InterruptedException e) {
                    throw new RuntimeException("创建watchKey发生异常", e);
                }
            }
        });

        watchDaemon.setDaemon(true);
        watchDaemon.start();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FileWatchService线程已启动");
        }
    }

    public Consumer<List<WatchEvent<?>>> getConsumers() {
        return consumers;
    }

    public void setConsumers(Consumer<List<WatchEvent<?>>> consumers) {
        this.consumers = consumers;
    }

    public Consumer<WatchEvent> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<WatchEvent> consumer) {
        this.consumer = consumer;
    }
}
