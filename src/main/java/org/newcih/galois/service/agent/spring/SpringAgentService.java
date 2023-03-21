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

import static org.newcih.galois.constants.ClassNameConstant.ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT;
import static org.newcih.galois.constants.ClassNameConstant.CLASS_PATH_BEAN_DEFINITION_SCANNER;
import static org.newcih.galois.constants.ClassNameConstant.SPRING_APPLICATION_RUN_LISTENERS;

import java.util.ArrayList;
import java.util.List;
import org.newcih.galois.service.agent.AgentService;
import org.newcih.galois.utils.GaloisLog;
import org.springframework.boot.SpringApplicationRunListener;

public class SpringAgentService extends AgentService {

  private static final SpringAgentService springAgent = new SpringAgentService();
  private static final GaloisLog logger = GaloisLog.getLogger(SpringAgentService.class);
  private final List<SpringApplicationRunListener> runners = new ArrayList<>(16);

  private SpringAgentService() {
    adapterMap.put(CLASS_PATH_BEAN_DEFINITION_SCANNER, new BeanDefinitionScannerVisitor());
    adapterMap.put(ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT,
        new ApplicationContextVisitor());
    adapterMap.put(SPRING_APPLICATION_RUN_LISTENERS, new SpringApplicationRunListenersVisitor());
    necessaryClasses.addAll(adapterMap.keySet());
  }

  public static SpringAgentService getInstance() {
    return springAgent;
  }

  @Override
  public void init() {
    super.init();
    listeners.add(new SpringBeanListener());
    beanReloader = SpringBeanReloader.getInstance();
  }

  /**
   * add runner
   *
   * @param runner
   */
  public void addRunner(SpringApplicationRunListener runner) {
    if (runner != null) {
      logger.info("add new started runner {}.", runner);
      runners.add(runner);
    }
  }

  public void addRunners(List<SpringApplicationRunListener> runners) {
    if (runners != null && runners.isEmpty()) {
      this.runners.addAll(runners);
    }
  }

  public List<SpringApplicationRunListener> getRunners() {
    return runners;
  }
}
