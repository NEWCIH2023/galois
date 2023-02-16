package org.newcih.service.agent;

import javassist.*;
import org.newcih.util.GaloisLog;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class SpringTransformer implements ClassFileTransformer {

    /**
     * Bean扫描服务类
     */
    public static final String SPRING_BEAN_SCANNER_PATH = "org/springframework/context/annotation/ClassPathBeanDefinitionScanner";
    public static final String SPRING_BEAN_SCANNER_CLASS = SPRING_BEAN_SCANNER_PATH.replace("/", ".");

    /**
     * Spring上下文服务类
     */
    public static final String SPRING_APPLICATION_CONTEXT_PATH = "org/springframework/boot/web/servlet/context/AnnotationConfigServletWebServerApplicationContext";
    public static final String SPRING_APPLICATION_CONTEXT_CLASS = SPRING_APPLICATION_CONTEXT_PATH.replace("/", ".");
    public static final GaloisLog LOGGER = GaloisLog.getLogger(SpringTransformer.class);

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className == null || className.trim().length() == 0) {
            return new byte[0];
        }

        switch (className) {
            case SPRING_APPLICATION_CONTEXT_PATH:
                LOGGER.info("find hacked class %s", className);
                return handleSpringApplicationContext(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            case SPRING_BEAN_SCANNER_PATH:
                LOGGER.info("find hacked class %s", className);
                return handleSpringBeanScanner(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            default:
        }

        return new byte[0];
    }

    private static byte[] handleSpringApplicationContext(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass applicationContext = classPool.get(SPRING_APPLICATION_CONTEXT_CLASS);
            CtConstructor constructor = applicationContext.getDeclaredConstructor(new CtClass[0]);

            LOGGER.info("find hacked constructor %s", constructor);

            constructor.insertAfter(String.format("{ if (this instanceof %s) { org.newcih.service.reloader.spring.SpringBeanReloader.registerApplicationContext((%s)this); } }", SPRING_APPLICATION_CONTEXT_CLASS, SPRING_APPLICATION_CONTEXT_CLASS));
            return applicationContext.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            LOGGER.error("hack method throw Exception", e);
            throw new RuntimeException(e);
        }

    }

    private static byte[] handleSpringBeanScanner(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass scanner = classPool.get(SPRING_BEAN_SCANNER_CLASS);
            CtMethod method = scanner.getDeclaredMethod("doScan");

            LOGGER.info("find hacked method %s", method);

            method.insertBefore(String.format("{ if (this instanceof %s) { org.newcih.service.reloader.spring.SpringBeanReloader.registerBeanReloader((%s)this); } }", SPRING_BEAN_SCANNER_CLASS, SPRING_BEAN_SCANNER_CLASS));

            return scanner.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            LOGGER.error("hacked method faild", e);
            throw new RuntimeException(e);
        }

    }
}
