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

import org.newcih.galois.service.agent.FileChangedListener;
import org.newcih.galois.utils.GaloisLog;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import static com.sun.nio.file.SensitivityWatchEventModifier.MEDIUM;
import static java.nio.file.StandardWatchEventKinds.*;
import static org.newcih.galois.constants.Constant.DOT;

/**
 * 基于Apache Common IO的文件变更监听工具
 */
public class FileWatchService {

    private final static GaloisLog logger = GaloisLog.getLogger(FileWatchService.class);
    private List<FileChangedListener> listeners;
    private WatchService watchService;
    private Thread watchThread;

    public FileWatchService(String rootPath) {
        if (rootPath == null || rootPath.isEmpty()) {
            throw new NullPointerException("empty path for galois listener!");
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();
            registerWatchService(new File(rootPath));
        } catch (IOException e) {
            logger.error("start file watchService failed", e);
            System.exit(0);
        }
    }

    /**
     * register each child directory to monitor file changed
     *
     * @param dir
     * @throws IOException
     */
    private void registerWatchService(File dir) throws IOException {
        dir.toPath().register(watchService, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE}, MEDIUM);
        File[] subDirs = dir.listFiles(File::isDirectory);
        if (subDirs == null) {
            return;
        }

        String dirName;
        for (File subDir : subDirs) {
            dirName = subDir.getName();
            if (!dirName.startsWith(DOT)) {
                registerWatchService(subDir);
            }
        }
    }

    /**
     * @param rootPath 监听路径
     */
    public FileWatchService(String rootPath, List<FileChangedListener> listeners) {
        this(rootPath);
        this.listeners = listeners;
    }

    public void start() {
        try {
            watchThread = new Thread(() -> {
                while (true) {
                    try {
                        WatchKey watchKey = watchService.take();
                        if (watchKey == null) {
                            continue;
                        }

                        for (WatchEvent<?> event : watchKey.pollEvents()) {
                            if (event.count() > 1) {
                                continue;
                            }

                            WatchEvent.Kind<?> kind = event.kind();

                            if (kind == OVERFLOW) {
                                continue;
                            }

                            File file = new File(watchKey.watchable() + File.separator + event.context());
                            if (file.isDirectory()) {
                                continue;
                            }

                            listeners.stream()
                                    .filter(listener -> listener.isUseful(file))
                                    .forEach(listener -> {
                                        if (kind == ENTRY_CREATE) {
                                            listener.createdHandle(file);
                                        } else if (kind == ENTRY_DELETE) {
                                            listener.deletedHandle(file);
                                        } else if (kind == ENTRY_MODIFY) {
                                            listener.modifiedHandle(file);
                                        }
                                    });
                        }

                        watchKey.reset();
                    } catch (Exception e) {
                        logger.error("file change handle failed", e);
                    }
                }
            });
            watchThread.setDaemon(true);
            watchThread.start();

            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

            if (logger.isDebugEnabled()) {
                logger.debug("file watch service had already started!");
            }
        } catch (Exception e) {
            logger.error("start file watch service failed!", e);
        }

    }

    public void stop() {
        try {
            if (watchThread != null) {
                watchThread.interrupt();
            }
        } catch (Exception e) {
            logger.error("close file watch service failed!", e);
        }
    }

    public List<FileChangedListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<FileChangedListener> listeners) {
        this.listeners = listeners;
    }
}
