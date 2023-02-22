package org.newcih.service.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.newcih.util.GaloisLog;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MyBatisTransformer implements ClassFileTransformer {

    public static final String SQL_SESSION_FACTORY_CLASS = "org.mybatis.spring.SqlSessionFactoryBean";
    public static final String SQL_SESSION_FACTORY_PATH = SQL_SESSION_FACTORY_CLASS.replace(".", "/");
    private static final GaloisLog LOGGER = GaloisLog.getLogger(MyBatisTransformer.class);

    private static byte[] handleSqlSessionFactoryConstructor(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain domain, byte[] classFileBuffer) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass factory = classPool.get(SQL_SESSION_FACTORY_CLASS);
            CtMethod method = factory.getDeclaredMethod("afterPropertiesSet");
            method.insertAfter("{ org.newcih.service.reloader.mybatis.MyBatisBeanReloader.factory = this" +
                    ".sqlSessionFactory; }");
            return factory.toBytecode();
        } catch (Exception e) {
            LOGGER.error("侵入MyBatis的SqlSessionFactory构造方法发生异常", e);
        }

        return new byte[0];
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null || className.trim().length() == 0) {
            return new byte[0];
        }

        if (className.equals(SQL_SESSION_FACTORY_PATH)) {
            return handleSqlSessionFactoryConstructor(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        }

        return new byte[0];
    }
}
