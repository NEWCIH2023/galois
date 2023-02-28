package org.newcih.service.agent.frame.mybatis;

import org.newcih.service.agent.MethodAdapter;
import org.newcih.utils.GaloisLog;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

/**
 * MyBatis框架相关类嵌入处理
 */
public class MyBatisTransformer implements ClassFileTransformer {

    private static final Map<String, MethodAdapter> mac = new HashMap<>(64);

    /**
     * SqlSessionBean管理工厂类
     */
    public static final String SQL_SESSION_FACTORY_BEAN = "org.apache.ibatis.session.defaults.DefaultSqlSessionFactory";
    private static final GaloisLog logger = GaloisLog.getLogger(MyBatisTransformer.class);

    static {
        mac.put(SQL_SESSION_FACTORY_BEAN, new SqlSessionFactoryBeanVisitor());
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className == null || className.trim().length() == 0) {
            return classfileBuffer;
        }

        String newClassName = className.replace("/", ".");

        switch (newClassName) {
            case SQL_SESSION_FACTORY_BEAN:
                return mac.get(newClassName).transform();
            default:
        }

        return classfileBuffer;
    }
}
