package org.newcih.service.agent.frame.mybatis;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import org.newcih.utils.GaloisLog;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;

import static org.newcih.service.agent.frame.spring.SpringTransformer.ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT;
import static org.newcih.service.agent.frame.spring.SpringTransformer.CLASS_PATH_BEAN_DEFINITION_SCANNER;

/**
 * MyBatis框架相关类嵌入处理
 */
public class MyBatisTransformer implements ClassFileTransformer {

    public static final String SQL_SESSION_FACTORY_BEAN = "org.apache.ibatis.session.defaults.DefaultSqlSessionFactory";
    private static final GaloisLog LOGGER = GaloisLog.getLogger(MyBatisTransformer.class);

    /**
     * 用于获取MyBatis的Configuration对象
     *
     * @param loader
     * @param className
     * @param classBeingRedefined
     * @param domain
     * @param classfileBuffer
     * @return
     */
    private byte[] handleSqlSessionFactoryConstructor(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain domain, byte[] classfileBuffer) {
        try {

            CtClass factory = ClassPool.getDefault().get(className);
            CtConstructor constructor = factory.getDeclaredConstructors()[0];
            String insertCode = String.format("%s.getInstance().registerConfiguration(this.configuration);",
                    MyBatisBeanReloader.class.getName());

            if (LOGGER.isDebugEnabled()) {
                insertCode += String.format("System.out.println(\"MyBatis的%s类的构造方法侵入成功\");", className);
            }

            constructor.insertAfter(insertCode);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("%s 正在处理 %s 类的 %s 方法", getClass().getSimpleName(), className, constructor.getName());
            }

            return factory.toBytecode();

        } catch (Exception e) {
            LOGGER.error("侵入MyBatis的DefaultSqlSessionFactory的构造方法发生异常", e);
        }

        return null;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className == null || className.trim().length() == 0) {
            return null;
        }

        String newClassName = className.replace("/", ".");

        if (LOGGER.isDebugEnabled()) {
            if (Arrays.asList(ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT,
                    CLASS_PATH_BEAN_DEFINITION_SCANNER, SQL_SESSION_FACTORY_BEAN).contains(newClassName)) {
                LOGGER.debug("%s 正在扫描 %s", getClass().getSimpleName(), newClassName);
            }
        }

        switch (newClassName) {
            case SQL_SESSION_FACTORY_BEAN:
                return handleSqlSessionFactoryConstructor(loader, newClassName, classBeingRedefined, protectionDomain,
                        classfileBuffer);
            default:
        }

        return classfileBuffer;
    }
}
