package org.newcih.service.watch.frame.mybatis;

import org.newcih.service.agent.frame.mybatis.MyBatisBeanReloader;
import org.newcih.service.watch.frame.FileChangedListener;
import org.newcih.utils.GaloisLog;

import java.io.File;

/**
 * MyBatis的XML文件变更监听处理
 */
public class MyBatisXmlListener implements FileChangedListener {

    private static final GaloisLog logger = GaloisLog.getLogger(MyBatisXmlListener.class);

    @Override
    public boolean validFile(File file) {
        return file.getName().endsWith(".xml");
    }

    @Override
    public void fileCreatedHandle(File file) {
        MyBatisBeanReloader reloader = MyBatisBeanReloader.getInstance();
        reloader.addBean(file);
        logger.info("Mybatis已重新加载%s文件", file.getName());
    }

    @Override
    public void fileModifiedHandle(File file) {
        MyBatisBeanReloader reloader = MyBatisBeanReloader.getInstance();
        reloader.addBean(file);
        logger.info("Mybatis已重新加载%s文件", file.getName());
    }

    @Override
    public void fileDeletedHandle(File file) {

    }
}
