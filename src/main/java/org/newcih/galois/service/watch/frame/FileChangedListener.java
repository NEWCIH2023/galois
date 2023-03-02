package org.newcih.galois.service.watch.frame;

import java.io.File;

/**
 * 文件变动监听
 */
public interface FileChangedListener {


    /**
     * 验证该文件是否适合该监听器处理
     *
     * @param file
     * @return
     */
    boolean validFile(File file);

    /**
     * 文件新增时处理
     *
     * @param file
     */
    void fileCreatedHandle(File file);

    /**
     * 文件修改时处理
     *
     * @param file
     */
    void fileModifiedHandle(File file);

    /**
     * 文件删除时处理
     *
     * @param file
     */
    void fileDeletedHandle(File file);

}
