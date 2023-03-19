/*
 * MIT License
 *
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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import org.newcih.galois.conf.GlobalConfiguration;
import org.newcih.galois.service.agent.FileChangedListener;
import org.newcih.galois.utils.GaloisLog;

import static com.sun.nio.file.SensitivityWatchEventModifier.MEDIUM;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static org.newcih.galois.constants.Constant.DOT;
import static org.newcih.galois.constants.Constant.USER_DIR;

/**
 * 基于Apache Common IO的文件变更监听工具
 */
public class FileWatchService {

    private final static GaloisLog logger = GaloisLog.getLogger(FileWatchService.class);
    private static List<FileChangedListener> listeners = new ArrayList<>(16);
    private WatchService watchService;
    private static final SpringBootLifeCycle lifeCycle = SpringBootLifeCycle.getInstance();
    private static final GlobalConfiguration globalConfig = GlobalConfiguration.getInstance();

    static {
        String rootPath = globalConfig.getString(USER_DIR);
        lifeCycle.addRunner((context) -> new FileWatchService(rootPath).start());
    }

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
     * @param dir root dir
     */
    private void registerWatchService(File dir) throws IOException {
        dir.toPath().register(watchService, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_MODIFY}, MEDIUM);
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
     * begin file monitor service
     */
    public void start() {
        Thread watchThread = new Thread(() -> {
            logger.info("file change handle service started.");

            while (true) {
                try {
                    WatchKey watchKey = watchService.take();
                    if (watchKey == null) {
                        continue;
                    }

                    for (WatchEvent<?> event : watchKey.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        File file = new File(watchKey.watchable() + File.separator + event.context());

                        if (event.count() > 1 || kind == OVERFLOW || file.isDirectory()) {
                            continue;
                        }

                        listeners.stream()
                                .filter(listener -> listener.isUseful(file))
                                .forEach(listener -> {
                                    if (kind == ENTRY_CREATE) {
                                        listener.createdHandle(file);
                                    } else if (kind == ENTRY_MODIFY) {
                                        listener.modifiedHandle(file);
                                    }
                                });
                    }

                    watchKey.reset();
                } catch (Exception e) {
                    logger.error("file change handle failed.", e);
                }
            }
        });

        watchThread.setDaemon(true);
        watchThread.start();
    }

    public List<FileChangedListener> getListeners() {
        return listeners;
    }

    public void registerListeners(FileChangedListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

}
