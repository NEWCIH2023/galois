package org.newcih.service;

/**
 * 文件监听服务
 */
public interface FileWatchService {

    /**
     * 启动文件监听
     *
     * @return 启动结果
     */
    boolean start();

    /**
     * 关闭文件监听
     *
     * @return 关闭结果
     */
    boolean stop();
}
