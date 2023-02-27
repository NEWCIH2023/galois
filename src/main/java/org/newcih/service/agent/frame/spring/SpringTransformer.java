package org.newcih.service.agent.frame.spring;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import org.newcih.utils.GaloisLog;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class SpringTransformer implements ClassFileTransformer {

    /**
     * Bean扫描服务类
     */
    public static final String CLASS_PATH_BEAN_DEFINITION_SCANNER =
            "org.springframework.context.annotation.ClassPathBeanDefinitionScanner";

    /**
     * Spring上下文服务类
     */
    public static final String ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT =
            "org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext";

    public static final GaloisLog logger = GaloisLog.getLogger(SpringTransformer.class);

    private byte[] handleSpringApplicationContext(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                                  ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        try {
            CtClass applicationContext = ClassPool.getDefault().get(className);
            CtConstructor constructor = applicationContext.getDeclaredConstructor(new CtClass[0]);

            String insertCode = String.format("%s.getInstance().registerApplicationContext(this);",
                    SpringBeanReloader.class.getName());

            if (logger.isDebugEnabled()) {
                insertCode += String.format("System.out.println(\"injected constructor method of %s class in spring " +
                        "success\");", className);
            }

            constructor.insertAfter(insertCode);

            if (logger.isDebugEnabled()) {
                logger.debug("%s is handling %s method in %s class", getClass().getSimpleName(),
                        constructor.getName(), className);
            }

            return applicationContext.toBytecode();
        } catch (Exception e) {
            logger.error("inject constructor method of %s class in spring fail", className, e);
        }

        return null;
    }

    private byte[] handleSpringBeanScanner(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                           ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        try {

            CtClass scanner = ClassPool.getDefault().get(className);
            CtMethod method = scanner.getDeclaredMethod("doScan");
            String insertCode = String.format("%s.getInstance().registerBeanReloader(this);",
                    SpringBeanReloader.class.getName());

            if (logger.isDebugEnabled()) {
                insertCode += String.format("System.out.println(\"Spring的%s类的%s方法侵入成功\");", className,
                        method.getName());
            }

            method.insertBefore(insertCode);

            if (logger.isDebugEnabled()) {
                logger.debug("%s 正在处理 %s 类的 %s 方法", getClass().getSimpleName(), className, method.getName());
            }

            return scanner.toBytecode();
        } catch (Exception e) {
            logger.error("侵入代码注册SpringBean管理器发生异常", e);
        }

        return null;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className == null || className.trim().length() == 0) {
            return null;
        }

        String newClassName = className.replace("/", ".");

        switch (newClassName) {
            case ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT:
                return handleSpringApplicationContext(loader, newClassName, classBeingRedefined, protectionDomain,
                        classfileBuffer);
            case CLASS_PATH_BEAN_DEFINITION_SCANNER:
                return handleSpringBeanScanner(loader, newClassName, classBeingRedefined, protectionDomain,
                        classfileBuffer);
            default:
        }

        return classfileBuffer;
    }
}
