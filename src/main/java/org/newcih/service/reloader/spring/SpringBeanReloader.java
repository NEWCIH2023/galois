package org.newcih.service.reloader.spring;

import org.newcih.service.reloader.BeanReloader;
import org.newcih.util.GaloisLog;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class SpringBeanReloader implements BeanReloader {

    private static ClassPathBeanDefinitionScanner SCANNER;

    private static AnnotationConfigApplicationContext APPLICATION_CONTEXT;

    private static final GaloisLog LOGGER = GaloisLog.getLogger(SpringBeanReloader.class);

    /**
     * 注册Bean扫描器
     *
     * @param scanner
     */
    public static void registerBeanReloader(ClassPathBeanDefinitionScanner scanner) {
        SCANNER = scanner;
    }

    public static void registerApplicationContext(AnnotationConfigApplicationContext applicationContext) {
        APPLICATION_CONTEXT = applicationContext;
    }

    @Override
    public void addBean(Class<?> clazz, Object bean) {
        LOGGER.info("即将使用%s添加类型为%s的对象bean<%s>", SCANNER, clazz, bean);
        String packageName = clazz.getPackage().getName();
        Set<BeanDefinition> beanDefinitionSet = SCANNER.findCandidateComponents(packageName);
        LOGGER.info("这个包%s有%d个装载类", packageName, beanDefinitionSet.size());

        Iterator<BeanDefinition> definitionIterator = beanDefinitionSet.iterator();
        BeanDefinition temp;
        while (definitionIterator.hasNext()) {
            temp = definitionIterator.next();
            if (Objects.equals(temp.getBeanClassName(), clazz.getName())) {
                SCANNER.getRegistry().removeBeanDefinition("demoController");
                SCANNER.clearCache();
                LOGGER.info("已清除旧实例定义，当前包下有%d个实例", SCANNER.findCandidateComponents(packageName).size());
                break;
            }
        }

        SCANNER.scan(packageName);
        LOGGER.info("已重新扫描，当前包下有%d个实例", SCANNER.findCandidateComponents(packageName).size());
    }

}
