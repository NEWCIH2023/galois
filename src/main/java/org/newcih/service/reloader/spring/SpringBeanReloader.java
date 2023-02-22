package org.newcih.service.reloader.spring;

import org.newcih.util.GaloisLog;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class SpringBeanReloader {

    private static final GaloisLog LOGGER = GaloisLog.getLogger(SpringBeanReloader.class);
    private static final SpringBeanReloader SPRING_BEAN_RELOADER = new SpringBeanReloader();
    private static ClassPathBeanDefinitionScanner SCANNER;
    private static ApplicationContext APPLICATION_CONTEXT;

    /**
     * 注册Bean扫描器
     *
     * @param scanner
     */
    public static void registerBeanReloader(ClassPathBeanDefinitionScanner scanner) {
        SCANNER = scanner;
    }

    /**
     * 注册Spring上下文对象
     *
     * @param applicationContext
     */
    public static void registerApplicationContext(ApplicationContext applicationContext) {
        APPLICATION_CONTEXT = applicationContext;
    }

    public static SpringBeanReloader getInstance() {
        return SPRING_BEAN_RELOADER;
    }

    public void addBean(Class<?> clazz, Object bean) {
        LOGGER.debug("即将使用%s添加类型为%s的对象bean<%s>", SCANNER, clazz, bean);

        String packageName = clazz.getPackage().getName();
        Set<BeanDefinition> beanDefinitionSet = SCANNER.findCandidateComponents(packageName);
        Iterator<BeanDefinition> definitionIterator = beanDefinitionSet.iterator();
        BeanDefinition temp;

        while (definitionIterator.hasNext()) {
            temp = definitionIterator.next();

            if (Objects.equals(temp.getBeanClassName(), clazz.getName())) {
                DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) APPLICATION_CONTEXT.getAutowireCapableBeanFactory();
                String beanName = beanFactory.getBeanNamesForType(clazz)[0];
                beanFactory.destroySingleton(beanName);
                beanFactory.registerSingleton(beanName, bean);
                LOGGER.debug("重新注册了%s", bean);
                break;
            }
        }
    }

}
