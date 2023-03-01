package org.newcih.service.watch.frame.spring;

import org.newcih.service.agent.frame.spring.SpringBeanReloader;
import org.newcih.service.watch.ProjectFileManager;
import org.newcih.service.watch.frame.FileChangedListener;
import org.newcih.utils.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

import static org.newcih.constants.FileTypeConstant.CLASS_FILE_TYPE;

/**
 * Spring的Bean变动监听器
 */
public final class SpringBeanListener implements FileChangedListener {

    public static final Logger logger = LoggerFactory.getLogger(SpringBeanListener.class);

    private final Instrumentation inst;

    private final static ProjectFileManager fileManager = ProjectFileManager.getInstance();

    public SpringBeanListener(Instrumentation inst) {
        this.inst = inst;
    }

    @Override
    public boolean validFile(File file) {
        return file.getName().endsWith(CLASS_FILE_TYPE);
    }


    @Override
    public void fileCreatedHandle(File file) {
        String className = SystemUtil.getClassName(fileManager.getClassPath(), file);

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
            logger.error("reload bean under file create event failed", e);
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
