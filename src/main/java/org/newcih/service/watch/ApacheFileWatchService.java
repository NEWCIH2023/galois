package org.newcih.service.watch;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.newcih.util.GaloisLog;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class ApacheFileWatchService extends FileAlterationListenerAdaptor implements FileWatchService {

    private final static GaloisLog LOGGER = GaloisLog.getLogger(ApacheFileWatchService.class);

    private Consumer<File> createHandler;

    private Consumer<File> modiferHandler;

    private Consumer<File> deleteHandler;

    private List<String> includeFileTypes;

    private List<String> excludeFileTypes;

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
        super.onStart(observer);
    }

    @Override
    public void onFileCreate(File file) {
        if (createHandler != null && isValidFile(file)) {
            createHandler.accept(file);
        }
    }

    @Override
    public void onFileChange(File file) {
        if (modiferHandler != null && isValidFile(file)) {
            modiferHandler.accept(file);
        }
    }

    @Override
    public void onFileDelete(File file) {
        if (deleteHandler != null && isValidFile(file)) {
            deleteHandler.accept(file);
        }
    }

    /**
     * 是否符合待监听文件类型
     *
     * @param file
     * @return
     */
    private boolean isValidFile(File file) {
        String fileName = file.getName();

        if (includeFileTypes != null && includeFileTypes.size() > 0) {
            return includeFileTypes.contains(fileName.substring(fileName.indexOf(".") + 1));
        } else if (excludeFileTypes != null && excludeFileTypes.size() > 0) {
            return !excludeFileTypes.contains(fileName.substring(fileName.indexOf(".") + 1));
        } else return false;

    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
    }

    public Consumer<File> getCreateHandler() {
        return createHandler;
    }

    public void setCreateHandler(Consumer<File> createHandler) {
        this.createHandler = createHandler;
    }

    public Consumer<File> getModiferHandler() {
        return modiferHandler;
    }

    public void setModiferHandler(Consumer<File> modiferHandler) {
        this.modiferHandler = modiferHandler;
    }

    public Consumer<File> getDeleteHandler() {
        return deleteHandler;
    }

    public void setDeleteHandler(Consumer<File> deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    public List<String> getIncludeFileTypes() {
        return includeFileTypes;
    }

    public void setIncludeFileTypes(List<String> includeFileTypes) {
        this.includeFileTypes = includeFileTypes;
    }

    public List<String> getExcludeFileTypes() {
        return excludeFileTypes;
    }

    public void setExcludeFileTypes(List<String> excludeFileTypes) {
        this.excludeFileTypes = excludeFileTypes;
    }
}
