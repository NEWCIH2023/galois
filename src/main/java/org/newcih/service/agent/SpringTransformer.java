package org.newcih.service.agent;

import javassist.*;
import org.newcih.service.reloader.spring.SpringBeanReloader;
import org.newcih.util.GaloisLog;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Objects;

public class SpringTransformer implements ClassFileTransformer {

    /**
     * Bean扫描服务类
     */
    public static final String SPRING_BEAN_SCANNER = "org/springframework/context/annotation/ClassPathBeanDefinitionScanner";

    /**
     * Spring上下文服务类
     */
    public static final String SPRING_APPLICATION_CONTEXT = "org/springframework/boot/web/servlet/context/AnnotationConfigServletWebServerApplicationContext";

    public static final GaloisLog LOGGER = GaloisLog.getLogger(SpringTransformer.class);

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null || className.trim().length() == 0) {
            return new byte[0];
        }

        LOGGER.info("find hacked class %s", className);
        switch (className) {
            case SPRING_APPLICATION_CONTEXT:
                return handleSpringApplicationContext(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            case SPRING_BEAN_SCANNER:
                return handleSpringBeanScanner(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            default:
        }

        return new byte[0];
    }

    private static byte[] handleSpringApplicationContext(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass applicationContext = classPool.get(SPRING_APPLICATION_CONTEXT.replace("/", "."));
            CtMethod method = applicationContext.getDeclaredMethod("postProcessBeanFactory");
            method.insertBefore("{ if ( this instanceof org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext ) { org.newcih.service.reloader.spring.SpringBeanReloader.registerBeanReloader((org.springframework.context.annotation.ClassPathBeanDefinitionScanner)this); } }");
            return applicationContext.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static byte[] handleSpringBeanScanner(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass scanner = classPool.get(SPRING_BEAN_SCANNER.replace("/", "."));
            CtMethod method = scanner.getDeclaredMethod("doScan");

            LOGGER.info("find hacked method %s", method);

            method.insertBefore("{ if (this instanceof org.springframework.context.annotation.ClassPathBeanDefinitionScanner ) { org.newcih.service.reloader.spring.SpringBeanReloader.registerBeanReloader((org.springframework.context.annotation.ClassPathBeanDefinitionScanner)this); } }");
            return scanner.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            LOGGER.error("hacked method faild", e);
            throw new RuntimeException(e);
        }

    }
}
