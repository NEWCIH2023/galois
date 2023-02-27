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

    public static final GaloisLog logger = GaloisLog.getLogger(SpringBeanListener.class);
    public static final String CLASS_FILE_SUFFIX = ".class";

    private final Instrumentation inst;

    public SpringBeanListener(Instrumentation inst) {
        this.inst = inst;
    }

    @Override
    public boolean validFile(File file) {
        return file.getName().endsWith(CLASS_FILE_SUFFIX);
    }

    /**
     * 文件变动处理
     *
     * @param changedFile
     */
    public void fileChangedHandler(File changedFile) {
        String classpath = SystemUtil.getOutputPath() + "classes" + File.separator;
        String className = SystemUtil.getClassName(classpath, changedFile);

        logger.info("检测到文件变动，当前需要加载%s", className);

        try {
            Class<?>[] classes = inst.getAllLoadedClasses();
            for (Class<?> clazz : classes) {

                if (clazz.getName().equals(className)) {
                    ClassDefinition newClassDef = new ClassDefinition(clazz, SystemUtil.readFile(changedFile));
                    inst.redefineClasses(newClassDef);

                    Object newBean = clazz.newInstance();
                    SpringBeanReloader.getInstance().updateBean(clazz, newBean);
                    break;
                }
            }
        } catch (Throwable e) {
            logger.error("重新加载实例对象的过程中发生异常", e);
        }
    }

    @Override
    public void fileCreatedHandle(File file) {
        fileChangedHandler(file);
    }

    @Override
    public void fileModifiedHandle(File file) {
        fileChangedHandler(file);
    }

    @Override
    public void fileDeletedHandle(File file) {

    }
}
