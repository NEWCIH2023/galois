/*
 * MIT License
 *
 * Copyright (c) [2023] [$user]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.newcih.galois.service.spring;

import java.lang.reflect.Modifier;
import org.newcih.galois.service.BeanReloader;
import org.newcih.galois.service.annotation.LazyBean;
import org.newcih.galois.service.spring.visitors.ApplicationContextVisitor;
import org.newcih.galois.service.spring.visitors.BeanDefinitionScannerVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * Spring的Bean重载服务
 *
 * @author liuguangsheng
 */
@LazyBean(value = "SpringBeanReloader", manager = SpringAgentService.class)
public class SpringBeanReloader implements BeanReloader<Class<?>>,
        ApplicationContextVisitor.NecessaryMethods, BeanDefinitionScannerVisitor.NecessaryMethods {

    private static final Logger logger = LoggerFactory.getLogger(SpringBeanReloader.class);
    protected ClassPathBeanDefinitionScanner scanner;
    protected AnnotationConfigServletWebServerApplicationContext context;
    private static final SpringBeanReloader instance = new SpringBeanReloader();

    public static SpringBeanReloader getInstance() {
        return instance;
    }

    /**
     * 更新Spring管理的bean对象
     *
     * @param clazz 待更新实例的类对象类型
     */
    @Override
    public void updateBean(Class<?> clazz) {
        if (scanner == null || context == null) {
            logger.error(
                    "SpringBeanReloader not prepare ready. BeanDefinitionScanner or ApplicationContext object is null" +
                            ".");
            return;
        }

        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) getContext().getAutowireCapableBeanFactory();
        String beanName = factory.getBeanNamesForType(clazz)[0];

        try {
            Object bean = clazz.newInstance();
            factory.destroySingleton(beanName);
            factory.registerSingleton(beanName, bean);
        } catch (InstantiationException ie) {
            logger.error(
                    "Can't create a new object by newInstance method, ensure that's not an abstract class.");
        } catch (Exception e) {
            logger.error("SpringBeanReloader update bean fail.", e);
        }

        logger.info("SpringBeanReloader reload class {} success.", clazz.getSimpleName());
    }

    @Override
    public boolean isUseful(Class<?> clazz) {
        int m = clazz.getModifiers();
        if (Modifier.isInterface(m) || Modifier.isAbstract(m) || Modifier.isPrivate(m)
                || Modifier.isStatic(m) || Modifier.isNative(m)) {
            return false;
        }

        String[] beanTypeNames = getContext().getBeanNamesForType(clazz);
        return beanTypeNames.length > 0;
    }

    /**
     * Gets scanner.
     *
     * @return the scanner
     */
    public ClassPathBeanDefinitionScanner getScanner() {
        return scanner;
    }

    /**
     * Sets scanner.
     *
     * @param scanner the scanner
     */
    @Override
    public void setScanner(ClassPathBeanDefinitionScanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public AnnotationConfigServletWebServerApplicationContext getContext() {
        return context;
    }

    /**
     * Sets context.
     *
     * @param context the context
     */
    @Override
    public void setContext(AnnotationConfigServletWebServerApplicationContext context) {
        this.context = context;
    }

}