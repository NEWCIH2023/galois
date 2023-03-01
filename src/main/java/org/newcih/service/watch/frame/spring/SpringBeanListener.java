package org.newcih.service.watch.frame.spring;

import org.newcih.service.agent.frame.spring.SpringBeanReloader;
import org.newcih.service.watch.frame.FileChangedListener;
import org.newcih.utils.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

/**
 * Spring的Bean变动监听器
 */
public final class SpringBeanListener implements FileChangedListener {

    public static final Logger logger = LoggerFactory.getLogger(SpringBeanListener.class);
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
    public void fileCreatedHandle(File file) {
        String classpath = SystemUtil.getOutputPath().replace("/", File.separator) + "classes" + File.separator;
        String className = SystemUtil.getClassName(classpath, file);

        logger.info("检测到文件变动，当前需要加载{}", className);

        try {
            Class<?>[] classes = inst.getAllLoadedClasses();
            for (Class<?> clazz : classes) {

                if (clazz.getName().equals(className)) {
                    ClassDefinition newClassDef = new ClassDefinition(clazz, SystemUtil.readFile(file));
                    inst.redefineClasses(newClassDef);

                    Object newBean = clazz.newInstance();
                    SpringBeanReloader.getInstance().updateBean(clazz, newBean);
                    break;
                }
            }
        } catch (Throwable e) {
            logger.error("重新加载实例对象的过程中发生异常", e);
            e.printStackTrace();
        }
    }

    @Override
    public void fileModifiedHandle(File file) {
        // 不可以在变动时处理，此时class文件经历删除-新增，该阶段可能找不到class文件
    }

    @Override
    public void fileDeletedHandle(File file) {

    }
}
