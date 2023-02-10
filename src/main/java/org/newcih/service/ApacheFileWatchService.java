package org.newcih.service;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.function.Consumer;

public class ApacheFileWatchService extends FileAlterationListenerAdaptor implements FileWatchService {

    private Consumer<File> createHandler;

    private Consumer<File> modiferHandler;

    private Consumer<File> deleteHandler;

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
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean stop() {
        try {
            monitor.stop();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
    }

    @Override
    public void onDirectoryCreate(File directory) {
        super.onDirectoryCreate(directory);
        if (createHandler != null) {
            createHandler.accept(directory);
        }
    }

    @Override
    public void onDirectoryChange(File directory) {
        super.onDirectoryChange(directory);
        if (modiferHandler != null) {
            modiferHandler.accept(directory);
        }
    }

    @Override
    public void onDirectoryDelete(File directory) {
        super.onDirectoryDelete(directory);
        if (deleteHandler != null) {
            deleteHandler.accept(directory);
        }
    }

    @Override
    public void onFileCreate(File file) {
        super.onFileCreate(file);
        if (createHandler != null) {
            createHandler.accept(file);
        }
    }

    @Override
    public void onFileChange(File file) {
        super.onFileChange(file);
        if (modiferHandler != null) {
            modiferHandler.accept(file);
        }
    }

    @Override
    public void onFileDelete(File file) {
        super.onFileDelete(file);
        if (deleteHandler != null) {
            deleteHandler.accept(file);
        }
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

}
