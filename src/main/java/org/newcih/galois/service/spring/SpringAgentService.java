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

import static org.newcih.galois.constants.ClassNameConstant.ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT;
import static org.newcih.galois.constants.ClassNameConstant.CLASS_PATH_BEAN_DEFINITION_SCANNER;
import static org.newcih.galois.constants.ClassNameConstant.SPRING_APPLICATION_RUN_LISTENERS;
import static org.newcih.galois.constants.ConfConstant.RELOADER_SPRING_BOOT_ENABLE;
import java.util.Map;
import org.newcih.galois.conf.GlobalConfiguration;
import org.newcih.galois.service.AgentService;
import org.newcih.galois.service.MethodAdapter;
import org.newcih.galois.service.PremainService;
import org.newcih.galois.service.spring.executors.SpringBeanReloader;
import org.newcih.galois.service.spring.listeners.SpringBeanListener;
import org.newcih.galois.service.spring.visitors.ApplicationContextVisitor;
import org.newcih.galois.service.spring.visitors.BeanDefinitionScannerVisitor;
import org.newcih.galois.service.spring.visitors.SpringApplicationRunListenersVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * spring agent service
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class SpringAgentService extends AgentService {

  private static final SpringAgentService springAgent = new SpringAgentService();
  private static final Logger logger = LoggerFactory.getLogger(SpringAgentService.class);
  private static final GlobalConfiguration globalConfig = GlobalConfiguration.getInstance();

  static {
    SpringAgentService service = SpringAgentService.getInstance();
    PremainService.registerAgentService(SpringAgentService.class.getSimpleName(), service);
  }

  private SpringAgentService() {
    adapterMap.put(CLASS_PATH_BEAN_DEFINITION_SCANNER, new BeanDefinitionScannerVisitor());
    adapterMap.put(ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT,
        new ApplicationContextVisitor());
    adapterMap.put(SPRING_APPLICATION_RUN_LISTENERS, new SpringApplicationRunListenersVisitor());
    necessaryClasses.addAll(adapterMap.keySet());
  }

  public static void registerVisitor(MethodAdapter methodAdapter) {
    Map<String, MethodAdapter> adapterMap = getInstance().getAdapterMap();
    adapterMap.put(methodAdapter.getClassName(), methodAdapter);
  }

  /**
   * 当前AgentService是否可启用
   *
   * @return 当项目已经加载了必须的类之后，该AgentService将成为可用状态
   */
  @Override
  public boolean isUseful() {
    return super.isUseful() && globalConfig.getBoolean(RELOADER_SPRING_BOOT_ENABLE);
  }

  /**
   * get instance
   *
   * @return {@link SpringAgentService}
   * @see SpringAgentService
   */
  public static SpringAgentService getInstance() {
    return springAgent;
  }

  @Override
  public void init() {
    listeners.add(new SpringBeanListener());
    beanReloader = SpringBeanReloader.getInstance();
  }


}
