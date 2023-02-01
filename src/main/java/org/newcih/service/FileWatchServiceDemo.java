package org.newcih.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class FileWatchServiceDemo {

    public static void main(String[] args) throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        String filePath = "C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\target";

        Paths.get(filePath).register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
        while (true) {
            WatchKey watchKey = watchService.take();
            List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
            for (WatchEvent<?> watchEvent : watchEvents) {
                WatchEvent.Kind<?> kind = watchEvent.kind();
                if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind)) {
                    System.out.println("检测到创建了 <" + filePath + "\\" + watchEvent.context() + ">");
                } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind)) {
                    System.out.println("检测到删除了 <" + filePath + "\\" + watchEvent.context() + ">");
                } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(kind)) {
                    System.out.println("检测到修改了 <" + filePath + "\\" + watchEvent.context() + ">");
                } else {
                    System.out.println("其它动作");
                }
            }
            watchKey.reset();
        }

    }
}
