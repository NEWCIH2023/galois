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

package org.newcih.galois.service.agent.spring;

import java.lang.reflect.Modifier;
import org.newcih.galois.conf.GlobalConfiguration;
import org.newcih.galois.service.agent.BeanReloader;
import org.newcih.galois.utils.GaloisLog;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * Spring的Bean重载服务
 */
public class SpringBeanReloader implements BeanReloader<Class<?>> {

  public static final GlobalConfiguration globalConfig = GlobalConfiguration.getInstance();
  private static final GaloisLog logger = GaloisLog.getLogger(SpringBeanReloader.class);
  private static final SpringBeanReloader springBeanReloader = new SpringBeanReloader();
  protected ClassPathBeanDefinitionScanner scanner;
  protected AnnotationConfigServletWebServerApplicationContext context;

  private SpringBeanReloader() {
  }

  /**
   * 获取单例实例
   */
  public static SpringBeanReloader getInstance() {
    return springBeanReloader;
  }

  /**
   * 更新Spring管理的bean对象
   *
   * @param clazz 待更新实例的类对象类型
   */
  @Override
  public void updateBean(Class<?> clazz) {
    if (scanner == null || context == null) {
      logger.error("springBeanReloader had not ready. scanner or context is null.");
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
          "can't create a new object from newInstance method, ensure that's not an abstract class.");
    } catch (Exception e) {
      logger.error("spring bean reloader update bean failed.", e);
    }

    logger.info("reload spring bean type {} success.", clazz.getName());
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

  public ClassPathBeanDefinitionScanner getScanner() {
    return scanner;
  }

  public void setScanner(ClassPathBeanDefinitionScanner scanner) {
    this.scanner = scanner;
  }

  public AnnotationConfigServletWebServerApplicationContext getContext() {
    return context;
  }

  public void setContext(AnnotationConfigServletWebServerApplicationContext context) {
    this.context = context;
  }
}