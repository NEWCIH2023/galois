/*
 * MIT License
 *
 * Copyright (c) [2023] [$user]
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

package io.liuguangsheng.galois.service.monitor;

import io.liuguangsheng.galois.conf.GlobalConfiguration;
import io.liuguangsheng.galois.utils.GaloisLog;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;

import static com.sun.nio.file.ExtendedWatchEventModifier.FILE_TREE;
import static com.sun.nio.file.SensitivityWatchEventModifier.MEDIUM;
import static io.liuguangsheng.galois.constants.Constant.COMMA;
import static io.liuguangsheng.galois.constants.Constant.TILDE;
import static io.liuguangsheng.galois.constants.Constant.USER_DIR;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * file monitor service based on {@link java.nio.file.WatchService}
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class FileWatchService {

    private static final Logger logger = new GaloisLog(FileWatchService.class);
    private static final List<FileChangedListener> listeners = new ArrayList<>(16);
    private static final GlobalConfiguration globalConfig = GlobalConfiguration.getInstance();
    private static final FileWatchService instance = new FileWatchService();
    private WatchService watchService;
    private String rootPath = globalConfig.getString(USER_DIR);
    /**
     * The constant ignorePaths.
     */
    public static final List<String> ignorePaths = Arrays.asList("target" + File.separator + "generated-sources",
            "target" + File.separator + "maven-status", "target" + File.separator + "test-classes");

    private FileWatchService() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static FileWatchService getInstance() {
        return instance;
    }

    /**
     * init
     */
    private void init() {
        if (rootPath == null || rootPath.isEmpty()) {
            throw new NullPointerException("Empty path for galois listener.");
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();
            Paths.get(rootPath).register(watchService, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE},
                    MEDIUM, FILE_TREE);
        } catch (IOException e) {
            logger.error("Start file watch service fail.", e);
            System.exit(0);
        }
    }

    /**
     * begin file monitor service
     */
    public void start() {
        init();

        logger.info("FileWatchService Started in path {} with these listeners {}", rootPath,
                listeners.stream().map(FileChangedListener::toString).collect(Collectors.joining(COMMA)));

        while (true) {
            try {
                // take()是一个阻塞方法，会等待监视器发出的信号才返回。
                // 还可以使用watcher.poll()方法，非阻塞方法，会立即返回当时监视器中是否有信号
                WatchKey watchKey = watchService.take();
                if (watchKey == null) {
                    continue;
                }

                List<WatchEvent<?>> events = watchKey.pollEvents();
                for (WatchEvent<?> event : events) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (event.context() == null || watchKey.watchable() == null) {
                        continue;
                    }

                    String fileName = event.context().toString();
                    if (fileName.endsWith(TILDE)) {
                        fileName = fileName.substring(0, fileName.length() - 1);
                    }
                    File file = new File(watchKey.watchable() + File.separator + fileName);

                    if (logger.isDebugEnabled()) {
                        if (event.count() > 1) {
                            logger.warn("[{}] monitor file {} {}", event.count(), kind, file);
                        } else {
                            logger.debug("[{}] monitor file {} {}", event.count(), kind, file);
                        }
                    }

                    if (event.count() > 1 || kind == OVERFLOW || file.isDirectory()) {
                        continue;
                    }

                    listeners.stream().filter(listener -> listener.isUseful(file)).forEach(listener -> {
                        if (kind == ENTRY_CREATE) {
                            listener.createdHandle(file);
                        } else if (kind == ENTRY_MODIFY) {
                            listener.modifiedHandle(file);
                        }
                    });
                }

                watchKey.reset();
            } catch (Throwable e) {
                logger.error("File monitor handle event failed.", e);
            }
        }
    }

    /**
     * Gets listeners.
     *
     * @return the listeners
     */
    public List<FileChangedListener> getListeners() {
        return listeners;
    }

    /**
     * add a new listener
     *
     * @param listener listener
     */
    public void registerListener(FileChangedListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * add listener list
     *
     * @param listeners listeners
     */
    public void registerListeners(List<FileChangedListener> listeners) {
        if (listeners != null && !listeners.isEmpty()) {
            FileWatchService.listeners.addAll(listeners);
        }
    }

    /**
     * Gets root path.
     *
     * @return the root path
     */
    public String getRootPath() {
        return rootPath;
    }

    /**
     * Sets root path.
     *
     * @param rootPath the root path
     */
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
