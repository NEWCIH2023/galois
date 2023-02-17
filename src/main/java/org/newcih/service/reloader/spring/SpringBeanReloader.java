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

    private static ClassPathBeanDefinitionScanner SCANNER;

    private static ApplicationContext APPLICATION_CONTEXT;

    private static final GaloisLog LOGGER = GaloisLog.getLogger(SpringBeanReloader.class);

    /**
     * 注册Bean扫描器
     *
     * @param scanner
     */
    public static void registerBeanReloader(ClassPathBeanDefinitionScanner scanner) {
        SCANNER = scanner;
    }

    public static void registerApplicationContext(ApplicationContext applicationContext) {
        APPLICATION_CONTEXT = applicationContext;
        LOGGER.info("now i got applicationContext %s", APPLICATION_CONTEXT);
    }

    public void addBean(Class<?> clazz, Object bean, ClassLoader classLoader) {
        LOGGER.info("即将使用%s添加类型为%s的对象bean<%s>", SCANNER, clazz, bean);

        if (!Objects.equals("DemoController", clazz.getSimpleName())) {
            return;
        }

        String packageName = clazz.getPackage().getName();
        Set<BeanDefinition> beanDefinitionSet = SCANNER.findCandidateComponents(packageName);
        Iterator<BeanDefinition> definitionIterator = beanDefinitionSet.iterator();
        BeanDefinition temp;

        while (definitionIterator.hasNext()) {
            temp = definitionIterator.next();

            if (Objects.equals(temp.getBeanClassName(), clazz.getName())) {
                DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) APPLICATION_CONTEXT.getAutowireCapableBeanFactory();
//                beanFactory.removeBeanDefinition("demoController");
                beanFactory.destroySingleton("demoController");
                beanFactory.setBeanClassLoader(classLoader);

//                AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(clazz).getBeanDefinition();
//                beanFactory.registerBeanDefinition("demoController", beanDefinition);

                try {
                    Class<?> reloadBean = clazz;
                    Class<?> appBean = SpringBeanReloader.class.getClassLoader().loadClass(clazz.getName());

                    LOGGER.info("%s from reload, %s from app", reloadBean, appBean);
                    Object reloadone = reloadBean.newInstance();
                    Object appone = appBean.newInstance();
                    LOGGER.info("equals two %s", Objects.equals(reloadone, appone));

                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                beanFactory.registerSingleton("demoController", bean);
                LOGGER.info("重新注册了%s", bean);
                break;
            }

        }

//        SCANNER.scan(packageName);
    }

}
