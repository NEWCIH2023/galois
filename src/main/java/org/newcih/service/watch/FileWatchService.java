package org.newcih.service.watch;

import java.io.File;
import java.util.function.Consumer;

/**
 * 文件监听服务
 */
public abstract class FileWatchService {

    protected Consumer<File> createHandler;

    protected Consumer<File> modiferHandler;

    protected Consumer<File> deleteHandler;

    /**
     * 启动文件监听
     *
     * @return 启动结果
     */
    public abstract boolean start();

    /**
     * 关闭文件监听
     *
     * @return 关闭结果
     */
    public abstract boolean stop();

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
