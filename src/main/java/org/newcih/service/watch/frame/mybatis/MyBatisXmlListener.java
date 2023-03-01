package org.newcih.service.watch.frame.mybatis;

import org.newcih.service.agent.frame.mybatis.MyBatisBeanReloader;
import org.newcih.service.watch.frame.FileChangedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * MyBatis的XML文件变更监听处理
 */
public class MyBatisXmlListener implements FileChangedListener {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisXmlListener.class);

    private static final MyBatisBeanReloader reloader = MyBatisBeanReloader.getInstance();

    @Override
    public boolean validFile(File file) {
        return file.getName().endsWith(".xml");
    }

    @Override
    public void fileCreatedHandle(File file) {
        reloader.updateBean(file);
    }

    @Override
    public void fileModifiedHandle(File file) {
        reloader.updateBean(file);
    }

    @Override
    public void fileDeletedHandle(File file) {

    }
}
