/*
 * MIT License
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

    private static final GaloisLog logger = GaloisLog.getLogger(SpringBeanReloader.class);
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
