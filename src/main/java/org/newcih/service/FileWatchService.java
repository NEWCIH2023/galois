package org.newcih.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * 文件变动监听服务
 *
 * @author liuguangsheng
 */
public class FileWatchService {

    /**
     * 监听服务对象
     */
    private WatchService watchService;

    /**
     * 监听变化后的动作
     */
    private Consumer<WatchEvent<?>> consumer;

    public FileWatchService(String[] paths) throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
        for (String path : paths) {
            Path tempPath = Paths.get(path);
            tempPath.register(watchService, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.OVERFLOW);
        }
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
                    for (WatchEvent<?> watchEvent : watchEvents) {
                        if (StandardWatchEventKinds.ENTRY_CREATE.equals(watchEvent.kind())) {
                            consumer.accept(watchEvent);
                        }
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException("创建watchKey发生异常", e);
                }
            }
        });

        watchDaemon.setDaemon(true);
        watchDaemon.start();
    }
}
