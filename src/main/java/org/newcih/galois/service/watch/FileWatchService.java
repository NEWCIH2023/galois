/*
 * MIT License
 * Copyright (c) [2023] [liuguangsheng]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.newcih.galois.service.watch;

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
