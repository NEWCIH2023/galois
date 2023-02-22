package org.newcih.service.watch.frame;

import org.newcih.service.reloader.mybatis.MyBatisBeanReloader;
import org.newcih.util.GaloisLog;

import java.io.File;
import java.lang.instrument.Instrumentation;

public class MyBatisXmlListener implements FileChangedListener {

    public static final GaloisLog LOGGER = GaloisLog.getLogger(MyBatisXmlListener.class);

    public MyBatisXmlListener() {
        registerListener(this);
    }

    @Override
    public boolean validFile(File file) {
        return file.getName().endsWith(".xml");
    }

    @Override
    public void fileCreatedHandle(File file, Instrumentation inst) {
        MyBatisBeanReloader reloader = MyBatisBeanReloader.getInstance();
        reloader.addBean(file);
        LOGGER.info("Mybatis已重新加载%s文件", file.getName());
    }

    @Override
    public void fileModifiedHandle(File file, Instrumentation inst) {
        MyBatisBeanReloader reloader = MyBatisBeanReloader.getInstance();
        reloader.addBean(file);
        LOGGER.info("Mybatis已重新加载%s文件", file.getName());
    }

    @Override
    public void fileDeletedHandle(File file, Instrumentation inst) {

    }
}
