package org.newcih.service.watch.frame.spring;

import org.newcih.service.agent.frame.spring.SpringBeanReloader;
import org.newcih.service.watch.frame.FileChangedListener;
import org.newcih.util.GaloisLog;
import org.newcih.util.SystemUtils;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

public final class SpringBeanListener implements FileChangedListener {

    public static final GaloisLog LOGGER = GaloisLog.getLogger(SpringBeanListener.class);
    public static final String CLASS_FILE_SUFFIX = ".class";

    @Override
    public boolean validFile(File file) {
        return file.getName().endsWith(CLASS_FILE_SUFFIX);
    }

    @Override
    public void fileCreatedHandle(File changedFile, Instrumentation inst) {
        String outputPath = SystemUtils.getOutputPath(null);
        String className = SystemUtils.getClassName(outputPath, changedFile);
        LOGGER.info("检测到文件变动，当前需要加载%s", className);

        try {
            Class<?>[] classes = inst.getAllLoadedClasses();
            for (Class<?> clazz : classes) {

                if (clazz.getName().equals(className)) {
                    ClassDefinition newClassDef = new ClassDefinition(clazz, SystemUtils.readFile(changedFile));
                    inst.redefineClasses(newClassDef);
                    LOGGER.info("已完成对类 %s 的重定义", clazz.getName());

                    Object newBean = clazz.newInstance();
                    SpringBeanReloader.getInstance().addBean(clazz, newBean);
                    LOGGER.info("已完成Spring对bean<%s>的重新装载", newBean);
                    break;
                }
            }
        } catch (Throwable e) {
            LOGGER.error("重新加载实例对象的过程中发生异常", e);
        }
    }

    @Override
    public void fileModifiedHandle(File file, Instrumentation inst) {

    }

    @Override
    public void fileDeletedHandle(File file, Instrumentation inst) {

    }
}
