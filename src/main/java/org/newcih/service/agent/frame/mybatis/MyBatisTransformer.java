package org.newcih.service.agent.frame.mybatis;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import org.newcih.utils.GaloisLog;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * MyBatis框架相关类嵌入处理
 */
public class MyBatisTransformer implements ClassFileTransformer {

    public static final String SQL_SESSION_FACTORY_BEAN = "org.apache.ibatis.session.defaults.DefaultSqlSessionFactory";
    private static final GaloisLog logger = GaloisLog.getLogger(MyBatisTransformer.class);

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
    private byte[] handleSqlSessionFactoryConstructor(ClassLoader loader, String className,
                                                      Class<?> classBeingRedefined, ProtectionDomain domain,
                                                      byte[] classfileBuffer) {
        try {

            CtClass factory = ClassPool.getDefault().get(className);
            CtConstructor constructor = factory.getDeclaredConstructors()[0];
            String insertCode = String.format("%s.getInstance().setConfiguration(this.configuration);",
                    MyBatisBeanReloader.class.getName());

            if (logger.isDebugEnabled()) {
                insertCode += String.format("System.out.println(\"injected constructor of %s class in mybatis " +
                        "success\");", className);
            }

            constructor.insertAfter(insertCode);

            if (logger.isDebugEnabled()) {
                logger.debug("%s is handling %s method in %s class", getClass().getSimpleName(),
                        constructor.getName(), className);
            }

            return factory.toBytecode();

        } catch (Exception e) {
            logger.error("inject constructor method of %s class failed", className);
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
            case SQL_SESSION_FACTORY_BEAN:
                return handleSqlSessionFactoryConstructor(loader, newClassName, classBeingRedefined, protectionDomain,
                        classfileBuffer);
            default:
        }

        return classfileBuffer;
    }
}
