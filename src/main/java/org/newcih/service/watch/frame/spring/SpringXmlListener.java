package org.newcih.service.watch.frame.spring;

import org.newcih.service.watch.frame.FileChangedListener;

import java.io.File;

/**
 * Spring的XML配置文件变更监听处理
 */
public class SpringXmlListener implements FileChangedListener {

    @Override
    public boolean validFile(File file) {
        return false;
    }

    @Override
    public void fileCreatedHandle(File file) {

    }

    @Override
    public void fileModifiedHandle(File file) {

    }

    @Override
    public void fileDeletedHandle(File file) {

    }
}
