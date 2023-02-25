package org.newcih.service.watch.frame.spring;

import org.newcih.service.agent.frame.spring.SpringBeanReloader;
import org.newcih.service.watch.frame.FileChangedListener;
import org.newcih.utils.GaloisLog;
import org.newcih.utils.SystemUtil;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

/**
 * Spring的Bean变动监听器
 */
public final class SpringBeanListener implements FileChangedListener {

    public static final GaloisLog LOGGER = GaloisLog.getLogger(SpringBeanListener.class);
    public static final String CLASS_FILE_SUFFIX = ".class";

    private final Instrumentation inst;

    public SpringBeanListener(Instrumentation inst) {
        this.inst = inst;
    }

    @Override
    public boolean validFile(File file) {
        return file.getName().endsWith(CLASS_FILE_SUFFIX);
    }

    @Override
    public void fileCreatedHandle(File changedFile) {
        String outputPath = SystemUtil.getOutputPath(null);
        String className = SystemUtil.getClassName(outputPath, changedFile);
        LOGGER.info("检测到文件变动，当前需要加载%s", className);

        try {
            Class<?>[] classes = inst.getAllLoadedClasses();
            for (Class<?> clazz : classes) {

                if (clazz.getName().equals(className)) {
                    ClassDefinition newClassDef = new ClassDefinition(clazz, SystemUtil.readFile(changedFile));
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
    public void fileModifiedHandle(File file) {

    }

    @Override
    public void fileDeletedHandle(File file) {

    }
}