package org.newcih.galois.service.watch.frame.spring;

import org.newcih.galois.service.agent.frame.spring.SpringBeanReloader;
import org.newcih.galois.service.watch.ProjectFileManager;
import org.newcih.galois.service.watch.frame.FileChangedListener;
import org.newcih.galois.utils.GaloisLog;
import org.newcih.galois.utils.SystemUtil;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

import static org.newcih.galois.constants.FileTypeConstant.CLASS_FILE_TYPE;

/**
 * Spring的Bean变动监听器
 */
public final class SpringBeanListener implements FileChangedListener {

    public static final GaloisLog logger = GaloisLog.getLogger(SpringBeanListener.class);
    private final static SpringBeanReloader reloader = SpringBeanReloader.getInstance();
    private final static ProjectFileManager fileManager = ProjectFileManager.getInstance();
    private final Instrumentation inst;

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

                    if (reloader.validBean(newBean)) {
                        reloader.updateBean(newBean);
                    }
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
