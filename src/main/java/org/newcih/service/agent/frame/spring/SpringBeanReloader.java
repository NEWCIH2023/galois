package org.newcih.service.agent.frame.spring;

import org.newcih.utils.GaloisLog;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Spring的Bean重载服务
 */
public final class SpringBeanReloader {

    private static final GaloisLog LOGGER = GaloisLog.getLogger(SpringBeanReloader.class);
    private static final SpringBeanReloader SPRING_BEAN_RELOADER = new SpringBeanReloader();

    private SpringBeanReloader() {
    }

    /**
     * 待注入属性
     */
    private ClassPathBeanDefinitionScanner scanner;
    private ApplicationContext applicationContext;

    /**
     * 通过Transform方式注册Bean扫描器
     *
     * @param scanner
     */
    public void registerBeanReloader(ClassPathBeanDefinitionScanner scanner) {
        getInstance().scanner = scanner;
    }

    /**
     * 通过Transform方式注册Spring上下文对象
     *
     * @param applicationContext
     */
    public void registerApplicationContext(ApplicationContext applicationContext) {
        getInstance().applicationContext = applicationContext;
    }

    /**
     * 获取单例实例
     *
     * @return
     */
    public static SpringBeanReloader getInstance() {
        return SPRING_BEAN_RELOADER;
    }

    public void addBean(Class<?> clazz, Object bean) {
        String packageName = clazz.getPackage().getName();
        Set<BeanDefinition> beanDefinitionSet = scanner.findCandidateComponents(packageName);
        Iterator<BeanDefinition> definitionIterator = beanDefinitionSet.iterator();
        BeanDefinition temp;

        while (definitionIterator.hasNext()) {
            temp = definitionIterator.next();

            if (Objects.equals(temp.getBeanClassName(), clazz.getName())) {
                DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
                String beanName = beanFactory.getBeanNamesForType(clazz)[0];
                beanFactory.destroySingleton(beanName);
                beanFactory.registerSingleton(beanName, bean);

                LOGGER.debug("register %s bean again", bean);
                break;
            }

        }
    }

}