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

package org.newcih.galois.service;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.newcih.galois.service.agent.FileChangedListener;
import org.newcih.galois.utils.GaloisLog;

import java.io.File;
import java.util.List;

/**
 * 基于Apache Common IO的文件变更监听工具
 */
public class ApacheFileWatchService implements FileAlterationListener {

    private final static GaloisLog logger = GaloisLog.getLogger(ApacheFileWatchService.class);
    private final FileAlterationMonitor monitor;
    private final FileAlterationObserver observer;

    private List<FileChangedListener> listeners;

    public ApacheFileWatchService(String path) {
        if (path == null || path.isEmpty()) {
            throw new NullPointerException("empty path for galois listener!");
        }

        observer = new FileAlterationObserver(new File(path));
        monitor = new FileAlterationMonitor(1000);
    }

    /**
     * @param path 监听路径
     */
    public ApacheFileWatchService(String path, List<FileChangedListener> listeners) {
        this(path);
        this.listeners = listeners;
    }

    public void start() {
        try {
            monitor.addObserver(observer);
            observer.addListener(this);
            monitor.start();
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

            if (logger.isDebugEnabled()) {
                logger.debug("filewatchservice had already started!");
            }
        } catch (Exception e) {
            logger.error("start filewatchservice failed!", e);
        }

    }

    public void stop() {
        try {
            monitor.stop();
        } catch (Exception e) {
            logger.error("close filewatchservice failed!", e);
        }
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
        if (listeners == null || listeners.isEmpty()) {
            return;
        }

        for (FileChangedListener listener : listeners) {
            if (listener.isUseful(file)) {
                listener.createdHandle(file);
            }
        }
    }

    @Override
    public void onFileChange(File file) {
        if (listeners == null || listeners.isEmpty()) {
            return;
        }

        for (FileChangedListener listener : listeners) {
            if (listener.isUseful(file)) {
                listener.modifiedHandle(file);
            }
        }
    }

    @Override
    public void onFileDelete(File file) {
        if (listeners == null || listeners.isEmpty()) {
            return;
        }

        for (FileChangedListener listener : listeners) {
            if (listener.isUseful(file)) {
                listener.deletedHandle(file);
            }
        }
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
