package org.newcih.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class FileWatchServiceDemo {

    public static final Logger LOGGER = LoggerFactory.getLogger(FileWatchServiceDemo.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        FileWatchService fileWatchService = new FileWatchService(new String[]{"C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\out", "C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\target"});
        fileWatchService.setConsumer(watchEvents -> {
            watchEvents.forEach(watchEvent -> {
                if (StandardWatchEventKinds.ENTRY_MODIFY.equals(watchEvent.kind())) {
                    LOGGER.info("检测到修改了 <" + watchEvent.context() + ">");
                }
            });
        });
        fileWatchService.action();

        Thread.currentThread().join();
    }
}
