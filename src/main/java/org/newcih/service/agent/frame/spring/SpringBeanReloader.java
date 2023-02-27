package org.newcih.service.agent.frame.spring;

import org.newcih.utils.GaloisLog;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Objects;
import java.util.Set;

/**
 * Spring的Bean重载服务
 */
public final class SpringBeanReloader {

    private static final GaloisLog logger = GaloisLog.getLogger(SpringBeanReloader.class);
    private static final SpringBeanReloader springBeanReloader = new SpringBeanReloader();

    private SpringBeanReloader() {
    }

    /**
     * 待注入属性
     */
    private ClassPathBeanDefinitionScanner scanner;
    private ApplicationContext applicationContext;

    /**
     * 获取单例实例
     *
     * @return
     */
    public static SpringBeanReloader getInstance() {
        return springBeanReloader;
    }

    /**
     * 判断当前bean的类是否归Spring管理
     *
     * @param clazz
     * @return
     */
    public boolean isSpringBean(Class<?> clazz) {
        DefaultListableBeanFactory beanFactory =
                (DefaultListableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
        String[] tmp = beanFactory.getBeanNamesForType(clazz);
        return tmp.length > 0;
    }

    /**
     * 更新Spring管理的bean对象
     *
     * @param clazz
     * @param bean
     */
    public void updateBean(Class<?> clazz, Object bean) {
        String packageName = clazz.getPackage().getName();
        Set<BeanDefinition> beanDefinitionSet = scanner.findCandidateComponents(packageName);

        DefaultListableBeanFactory beanFactory = null;
        for (BeanDefinition definition : beanDefinitionSet) {
            if (Objects.equals(definition.getBeanClassName(), clazz.getName())) {
                beanFactory = (DefaultListableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
                String beanName = beanFactory.getBeanNamesForType(clazz)[0];
                beanFactory.destroySingleton(beanName);
                beanFactory.registerSingleton(beanName, bean);
                break;
            }
        }
    }

    public ClassPathBeanDefinitionScanner getScanner() {
        return scanner;
    }

    public void setScanner(ClassPathBeanDefinitionScanner scanner) {
        this.scanner = scanner;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
