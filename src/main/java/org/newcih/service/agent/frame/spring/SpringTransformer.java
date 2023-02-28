package org.newcih.service.agent.frame.spring;

import org.newcih.service.agent.MethodAdapter;
import org.newcih.utils.GaloisLog;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class SpringTransformer implements ClassFileTransformer {

    private static final Map<String, MethodAdapter> mac = new HashMap<>(64);

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

    static {
        mac.put(CLASS_PATH_BEAN_DEFINITION_SCANNER, new BeanDefinitionScannerVisitor());
        mac.put(ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT, new ApplicationContextVisitor());
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className == null || className.trim().length() == 0) {
            return classfileBuffer;
        }

        String newClassName = className.replace("/", ".");

        switch (newClassName) {
            case ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT:
            case CLASS_PATH_BEAN_DEFINITION_SCANNER:
                return mac.get(newClassName).transform();
            default:
        }

        return classfileBuffer;
    }
}
