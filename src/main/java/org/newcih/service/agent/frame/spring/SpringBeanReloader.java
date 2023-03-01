package org.newcih.service.agent.frame.spring;

import org.newcih.service.agent.BeanReloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Objects;
import java.util.Set;

/**
 * Spring的Bean重载服务
 */
public final class SpringBeanReloader implements BeanReloader<Object> {

    private static final Logger logger = LoggerFactory.getLogger(SpringBeanReloader.class);
    private static final SpringBeanReloader springBeanReloader = new SpringBeanReloader();
    /**
     * 待注入属性
     */
    private ClassPathBeanDefinitionScanner scanner;
    private ApplicationContext applicationContext;

    private SpringBeanReloader() {
    }

    /**
     * 获取单例实例
     *
     * @return
     */
    public static SpringBeanReloader getInstance() {
        return springBeanReloader;
    }

    /**
     * 更新Spring管理的bean对象
     *
     * @param bean
     */
    @Override
    public void updateBean(Object bean) {
        Class<?> clazz = bean.getClass();
        String packageName = clazz.getPackage().getName();
        Set<BeanDefinition> beanDefinitionSet = scanner.findCandidateComponents(packageName);

        for (BeanDefinition definition : beanDefinitionSet) {
            if (Objects.equals(definition.getBeanClassName(), clazz.getName())) {
                DefaultListableBeanFactory beanFactory =
                        (DefaultListableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
                String beanName = beanFactory.getBeanNamesForType(clazz)[0];
                beanFactory.destroySingleton(beanName);
                beanFactory.registerSingleton(beanName, bean);

                if (logger.isDebugEnabled()) {
                    logger.debug("had reload spring bean {}", bean);
                }

                break;
            }
        }
    }

    @Override
    public boolean validBean(Object object) {
        Class<?> clazz = object.getClass();
        String[] beanTypeNames = getApplicationContext().getBeanNamesForType(clazz);
        return beanTypeNames.length > 0;
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
