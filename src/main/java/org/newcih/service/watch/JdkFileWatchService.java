package org.newcih.service.watch;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.newcih.util.GaloisLog;

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
public class JdkFileWatchService implements FileWatchService {

    public static final GaloisLog LOGGER = GaloisLog.getLogger(JdkFileWatchService.class);

    /**
     * 监听服务对象
     */
    private final WatchService watchService;

    private Consumer<WatchEvent<Path>> modifyHandler;

    private Consumer<WatchEvent<Path>> createHandler;

    private Consumer<WatchEvent<Path>> deleteHandler;

    public JdkFileWatchService(String[] paths) throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
        for (String path : paths) {
            Path tempPath = Paths.get(path);
            // macOS平台JDK不支持Modifer参数
            WatchEvent.Kind<?>[] kinds = new WatchEvent.Kind<?>[]{ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE};
            tempPath.register(watchService, kinds, SensitivityWatchEventModifier.HIGH);
        }
    }

    /**
     * 启动方法
     */
    @Override
    public boolean start() {
        try {
            addShutdownHook();
            registerWatchService();
        } catch (Exception e) {
            LOGGER.error("启动jdkWatchService服务发生异常", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean stop() {
        try {
            watchService.close();
        } catch (IOException e) {
            LOGGER.error("关闭jdkWatchService服务发生异常", e);
            return false;
        }

        return true;
    }

    /**
     * 注册watchService关闭hook
     */
    private void addShutdownHook() {
        Thread shutdownHook = new Thread(this::stop);
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        LOGGER.debug("FileWatchService注册了ShutdownHook");
    }

    /**
     * 注册watchService服务
     */
    @SuppressWarnings("unchecked")
    private void registerWatchService() {
        Thread watchDaemon = new Thread(() -> {
            while (true) {
                try {
                    WatchKey watchKey = watchService.take();
                    List<WatchEvent<?>> events = watchKey.pollEvents();

                    Thread.sleep(1000);

                    events.stream().distinct().filter(event -> event.count() <= 1)
                            .map(event -> (WatchEvent<Path>) event)
                            .forEach(event -> {
                                WatchEvent.Kind<Path> kind = event.kind();
                                if (ENTRY_CREATE.equals(kind)) {
                                    createHandler.accept(event);
                                } else if (ENTRY_MODIFY.equals(kind)) {
                                    modifyHandler.accept(event);
                                } else if (ENTRY_DELETE.equals(kind)) {
                                    deleteHandler.accept(event);
                                }
                            });

                    watchKey.reset();
                } catch (Exception e) {
                    throw new RuntimeException("FileWatchService处理监听项目发生异常", e);
                }
            }
        });

        watchDaemon.setDaemon(true);
        watchDaemon.start();

        LOGGER.debug("FileWatchService线程已启动");
    }

    public Consumer<WatchEvent<Path>> getModifyHandler() {
        return modifyHandler;
    }

    public void setModifyHandler(Consumer<WatchEvent<Path>> modifyHandler) {
        this.modifyHandler = modifyHandler;
    }

    public Consumer<WatchEvent<Path>> getCreateHandler() {
        return createHandler;
    }

    public void setCreateHandler(Consumer<WatchEvent<Path>> createHandler) {
        this.createHandler = createHandler;
    }

    public Consumer<WatchEvent<Path>> getDeleteHandler() {
        return deleteHandler;
    }

    public void setDeleteHandler(Consumer<WatchEvent<Path>> deleteHandler) {
        this.deleteHandler = deleteHandler;
    }
}
