package org.newcih.service.watch;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.newcih.util.GaloisLog;

import java.io.File;

/**
 * 基于Apache Common IO的文件变更监听工具
 */
public class ApacheFileWatchService extends FileWatchService implements FileAlterationListener {

    private final static GaloisLog LOGGER = GaloisLog.getLogger(ApacheFileWatchService.class);
    private final FileAlterationMonitor monitor;
    private final FileAlterationObserver observer;

    public ApacheFileWatchService(String path) {
        this(path, 500);
    }

    /**
     * @param path     监听路径
     * @param interval 监听时延
     */
    public ApacheFileWatchService(String path, long interval) {
        observer = new FileAlterationObserver(new File(path));
        monitor = new FileAlterationMonitor(interval);
    }

    @Override
    public boolean start() {
        try {
            monitor.addObserver(observer);
            observer.addListener(this);
            monitor.start();

            LOGGER.debug("FileWatchService文件监听服务已启动！");
        } catch (Exception e) {
            LOGGER.error("启动文件监听服务FileWatchService失败", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean stop() {
        try {
            monitor.stop();
        } catch (Exception e) {
            LOGGER.error("关闭文件监听服务FileWatchService失败", e);
            return false;
        }

        return true;
    }

    @Override
    public void onStart(FileAlterationObserver observer) {

    }

    @Override
    public void onDirectoryCreate(File directory) {

    }

    @Override
    public void onDirectoryChange(File directory) {

    }

    @Override
    public void onDirectoryDelete(File directory) {

    }

    @Override
    public void onFileCreate(File file) {
        LOGGER.debug("文件创建监听: %s", file.getName());

        if (createHandler != null) {
            createHandler.accept(file);
        }
    }

    @Override
    public void onFileChange(File file) {
        LOGGER.debug("文件更新监听: %s", file.getName());

        if (modiferHandler != null) {
            modiferHandler.accept(file);
        }
    }

    @Override
    public void onFileDelete(File file) {
        LOGGER.debug("文件删除监听: %s", file.getName());

        if (deleteHandler != null) {
            deleteHandler.accept(file);
        }
    }

    @Override
    public void onStop(FileAlterationObserver observer) {

    }
}
