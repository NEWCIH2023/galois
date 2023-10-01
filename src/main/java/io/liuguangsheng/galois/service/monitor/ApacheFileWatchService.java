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

package io.liuguangsheng.galois.service.monitor;

import io.liuguangsheng.galois.utils.GaloisLog;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;

import java.io.FileFilter;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.liuguangsheng.galois.constants.Constant.COMMA;

/**
 * The type Apache file watch service.
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class ApacheFileWatchService extends FileWatchService {
    private static final Logger logger = new GaloisLog(ApacheFileWatchService.class);
    private static final long interval = 1000;

    private static class ApacheFileWatchServiceHolder {
        private static final ApacheFileWatchService instance = new ApacheFileWatchService();
    }

    public static ApacheFileWatchService getInstance() {
        return ApacheFileWatchServiceHolder.instance;
    }

    @Override
    public void start() {
        URL buildUrl = getClass().getClassLoader().getResource("");
        String buildPath = Objects.requireNonNull(buildUrl, "Can't get build path by classLoader.").getPath();
        FileFilter fileFilter = pathname -> !pathname.toURI().getPath().startsWith(buildPath);
        FileAlterationObserver observer = new FileAlterationObserver(rootPath, fileFilter);

        try {
            listeners.stream().map(ApacheFileChangedListener::new).forEach(observer::addListener);
            observer.initialize();
            observer.checkAndNotify();
            new FileAlterationMonitor(interval, observer).start();
        } catch (Exception e) {
            logger.error("Invoke apache file monitor service failed.", e);
        }

        List<String> listenerNames = listeners.stream().map(FileChangedListener::toString).collect(Collectors.toList());
        String listenerNameStr = String.join(COMMA, listenerNames);

        logger.info("ApacheFileWatchService Started in path {} with {} listeners {}.", rootPath, listenerNames.size()
                , listenerNameStr);
    }
}
