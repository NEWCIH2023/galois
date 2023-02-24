package org.newcih.service.watch;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.newcih.service.watch.frame.FileChangedListener;
import org.newcih.utils.GaloisLog;

import java.io.File;
import java.util.List;

/**
 * 基于Apache Common IO的文件变更监听工具
 */
public class ApacheFileWatchService implements FileAlterationListener {

    private final static GaloisLog LOGGER = GaloisLog.getLogger(ApacheFileWatchService.class);
    private final FileAlterationMonitor monitor;
    private final FileAlterationObserver observer;

    private List<FileChangedListener> listeners;

    public ApacheFileWatchService(String path) {
        observer = new FileAlterationObserver(new File(path));
        monitor = new FileAlterationMonitor(500);
    }

    /**
     * @param path 监听路径
     */
    public ApacheFileWatchService(String path, List<FileChangedListener> listeners) {
        this(path);
        this.listeners = listeners;
    }

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

        if (listeners == null || listeners.isEmpty()) {
            return;
        }

        listeners.stream().filter(listener -> listener.validFile(file)).forEach(listener -> listener.fileCreatedHandle(file));
    }

    @Override
    public void onFileChange(File file) {
        LOGGER.debug("文件更新监听: %s", file.getName());

        if (listeners == null || listeners.isEmpty()) {
            return;
        }

        listeners.stream().filter(listener -> listener.validFile(file)).forEach(listener -> listener.fileCreatedHandle(file));
    }

    @Override
    public void onFileDelete(File file) {
        LOGGER.debug("文件删除监听: %s", file.getName());

        if (listeners == null || listeners.isEmpty()) {
            return;
        }

        listeners.stream().filter(listener -> listener.validFile(file)).forEach(listener -> listener.fileCreatedHandle(file));
    }

    @Override
    public void onStop(FileAlterationObserver observer) {

    }

    public List<FileChangedListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<FileChangedListener> listeners) {
        this.listeners = listeners;
    }
}
