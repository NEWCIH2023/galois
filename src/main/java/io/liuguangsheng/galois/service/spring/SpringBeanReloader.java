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

package io.liuguangsheng.galois.service.spring;

import io.liuguangsheng.galois.service.BeanReloader;
import io.liuguangsheng.galois.service.annotation.LazyBean;
import io.liuguangsheng.galois.service.spring.visitors.ApplicationContextVisitor;
import io.liuguangsheng.galois.service.spring.visitors.BeanDefinitionScannerVisitor;
import io.liuguangsheng.galois.utils.GaloisLog;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Spring的Bean重载服务
 *
 * @author liuguangsheng
 */
@LazyBean(value = "SpringBeanReloader", manager = SpringAgentService.class)
public class SpringBeanReloader implements
    BeanReloader<Class<?>>,
    ApplicationContextVisitor.NecessaryMethods,
    BeanDefinitionScannerVisitor.NecessaryMethods {

  private static final Logger logger = new GaloisLog(SpringBeanReloader.class);
  private static final SpringBeanReloader instance = new SpringBeanReloader();
  /**
   * The Scanner.
   */
  protected ClassPathBeanDefinitionScanner scanner;
  /**
   * The Context.
   */
  protected AnnotationConfigServletWebServerApplicationContext context;

  /**
   * Gets instance.
   *
   * @return the instance
   */
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
      logger.warn(
          "SpringBeanReloader not prepare ready. BeanDefinitionScanner or ApplicationContext object is null.");
      return;
    }

    DefaultListableBeanFactory factory = (DefaultListableBeanFactory) getContext().getAutowireCapableBeanFactory();
    String beanName = factory.getBeanNamesForType(clazz)[0];

    try {
      Object bean = clazz.newInstance();

      if (isHandler(clazz)) {
        updateRequestMapping(bean);
      } else {
        factory.destroySingleton(beanName);
        factory.registerSingleton(beanName, bean);
      }
    } catch (InstantiationException ie) {
      logger.error(
          "Can't create a new object by newInstance method, ensure that's not an abstract class.");
    } catch (Throwable e) {
      logger.error("SpringBeanReloader update bean fail.", e);
    }

    logger.info("SpringBeanReloader reload class {} success.", clazz.getSimpleName());
  }

  /**
   * update request mapping
   *
   * @param bean
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  private void updateRequestMapping(Object bean)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    RequestMappingHandlerMapping handlerMapping = getContext().getBean(
        RequestMappingHandlerMapping.class);
    Method updateHandlerMethods = handlerMapping.getClass()
        .getMethod("updateHandlerMethods", Object.class);
    updateHandlerMethods.invoke(handlerMapping, bean);
  }

  /**
   * is controller
   *
   * @param clazz changed class
   */
  private boolean isHandler(Class<?> clazz) {
    return clazz != null &&
        (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(
            RequestMapping.class));
  }

  /**
   * is useful
   *
   * @param clazz clazz
   * @return {@link boolean}
   */
  @Override
  public boolean isUseful(Class<?> clazz) {
    int m = clazz.getModifiers();
    if (Modifier.isInterface(m) ||
        Modifier.isAbstract(m) ||
        Modifier.isPrivate(m) ||
        Modifier.isStatic(m) ||
        Modifier.isNative(m)) {
      return false;
    }

    return true;
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