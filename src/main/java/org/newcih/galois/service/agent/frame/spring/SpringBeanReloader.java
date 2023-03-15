/*
 * MIT License
 *
 * Copyright (c) [2023] [liuguangsheng]
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

package org.newcih.galois.service.agent.frame.spring;

import org.newcih.galois.service.agent.BeanReloader;
import org.newcih.galois.utils.GaloisLog;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * Spring的Bean重载服务
 */
public final class SpringBeanReloader implements BeanReloader<Class<?>> {

    private static final GaloisLog logger = GaloisLog.getLogger(SpringBeanReloader.class);
    private static final SpringBeanReloader springBeanReloader = new SpringBeanReloader();
    public static final List<String> ignorePackages = Arrays.asList("org.springframework");
    /**
     * 待注入属性
     */
    private ClassPathBeanDefinitionScanner scanner;
    private ApplicationContext context;

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
     * @param clazz
     */
    @Override
    public void updateBean(Class<?> clazz) {
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) getContext().getAutowireCapableBeanFactory();
        String beanName = factory.getBeanNamesForType(clazz)[0];
        Object bean = null;

        try {
            bean = clazz.newInstance();
            factory.destroySingleton(beanName);
            factory.registerSingleton(beanName, bean);
        } catch (InstantiationException ie) {
            logger.error("can't create a new object from newInstance method, ensure that's not an abstract class");
        } catch (Exception e) {
            logger.error("spring bean reloader update bean failed", e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("had reload spring bean {}", bean);
        }
    }

    @Override
    public boolean isUseful(Class<?> clazz) {
        String className = clazz.getName();
        boolean isIgnoredClass = ignorePackages.stream().anyMatch(className::startsWith);
        if (isIgnoredClass) {
            return false;
        }

        int m = clazz.getModifiers();
        if (Modifier.isInterface(m) || Modifier.isAbstract(m) || Modifier.isPrivate(m) || Modifier.isStatic(m) || Modifier.isNative(m)) {
            return false;
        }

        String[] beanTypeNames = getContext().getBeanNamesForType(clazz);
        return beanTypeNames.length > 0;
    }

    public ClassPathBeanDefinitionScanner getScanner() {
        return scanner;
    }

    public void setScanner(ClassPathBeanDefinitionScanner scanner) {
        this.scanner = scanner;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}
