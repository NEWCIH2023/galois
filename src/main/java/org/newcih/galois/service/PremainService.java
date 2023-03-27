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

package org.newcih.galois.service;

import static org.newcih.galois.constants.Constant.DOT;
import static org.newcih.galois.constants.Constant.SLASH;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.newcih.galois.service.runners.AgentServiceInitRunner;
import org.newcih.galois.utils.JavaUtil;
import org.newcih.galois.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * premain agent服务入口
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class PremainService {

  private static final Logger logger = LoggerFactory.getLogger(PremainService.class);
  private static final Map<String, AgentService> agentServiceMap = new HashMap<>(8);
  private static final AgentServiceInitRunner initRunner = new AgentServiceInitRunner();
  private static final SpringRunnerManager runManager = SpringRunnerManager.getInstance();

  static {
    runManager.addRunner(initRunner);
  }

  /**
   * premain entry
   *
   * @param agentArgs agent args
   * @param inst      instrument object
   */
  public static void premain(String agentArgs, Instrumentation inst) {
    if (inst == null) {
      logger.error("Your program do not support instrumentation.");
      System.exit(0);
    }

    try {
      ClassFileTransformer custom = new CustomTransformer();
      inst.addTransformer(custom, true);
      JavaUtil.setInstrumentation(inst);
      BannerService.printBanner();
    } catch (Throwable e) {
      logger.error("Start Premain Service fail.", e);
    }
  }

  /**
   * Register agent service.
   *
   * @param serviceName  the service name
   * @param agentService the agent service
   */
  public static void registerAgentService(String serviceName, AgentService agentService) {
    if (agentServiceMap.containsKey(serviceName)) {
      return;
    }

    agentServiceMap.put(serviceName, agentService);
    // register spring runner
    initRunner.addAgentService(agentService);
  }

  /**
   * get agent services
   *
   * @return {@link Collection}
   * @see Collection
   * @see AgentService
   */
  public static Collection<AgentService> getAgentServices() {
    return agentServiceMap.values();
  }

  /**
   * custom class file transformer
   *
   * @author liuguangsheng
   */
  static class CustomTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classfileBuffer) {
      if (StringUtil.isBlank(className)) {
        return null;
      }

      String newClassName = className.replace(SLASH, DOT);
      Collection<AgentService> agentServices = agentServiceMap.values();

      for (AgentService agentService : agentServices) {
        boolean checkedClass = agentService.checkNecessaryClass(newClassName);
        // checkedClass表示当前加载的类newClassName是否有对应的MethodAdapter，当为false时，表示没有对应的MethodAdapter，
        // 这时候就直接跳过
        if (!checkedClass) {
          continue;
        }

        MethodAdapter adapter = agentService.getAdapterMap().get(newClassName);
        if (logger.isDebugEnabled()) {
          logger.debug("Instrumentation had retransformed class {}.", newClassName);
        }

        return adapter.transform();
      }

      return null;
    }
  }


}
