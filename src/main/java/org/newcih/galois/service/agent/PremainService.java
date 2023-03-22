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

package org.newcih.galois.service.agent;

import static java.util.stream.Collectors.joining;
import static org.newcih.galois.constants.Constant.DOT;
import static org.newcih.galois.constants.Constant.SLASH;
import static org.newcih.galois.constants.Constant.USER_DIR;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.newcih.galois.conf.GlobalConfiguration;
import org.newcih.galois.service.BannerService;
import org.newcih.galois.service.FileWatchService;
import org.newcih.galois.service.agent.corm.CormAgentService;
import org.newcih.galois.service.agent.mybatis.MyBatisAgentService;
import org.newcih.galois.service.agent.spring.SpringAgentService;
import org.newcih.galois.service.runner.FileWatchRunner;
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

  private static final GlobalConfiguration globalConfig = GlobalConfiguration.getInstance();
  private static final FileWatchService fileWatchService = FileWatchService.getInstance();
  private static final Logger logger = LoggerFactory.getLogger(PremainService.class);
  private static final SpringAgentService springAgentService = SpringAgentService.getInstance();
  private static final MyBatisAgentService mybatisAgentService = MyBatisAgentService.getInstance();
  private static final CormAgentService cormAgentService = CormAgentService.getInstance();
  private static final List<AgentService> agentServices = Arrays.asList(mybatisAgentService,
      cormAgentService, springAgentService);

  static {
    String rootPath = globalConfig.getString(USER_DIR);
    springAgentService.addRunner(new FileWatchRunner(rootPath));
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

    inst.addTransformer(new CustomTransformer(), true);
    JavaUtil.setInst(inst);
    BannerService.printBanner();
  }

  /**
   * print current loading state of agent service
   */
  private static void printAgentState() {
    List<AgentService> enabledAgents = agentServices.stream()
        .filter(AgentService::isInited)
        .collect(Collectors.toList());

    String enableAgentNames = enabledAgents.stream()
        .map(Object::toString)
        .collect(joining(","));

    String listenerNames = enabledAgents.stream()
        .flatMap(agent -> agent.getListeners().stream())
        .map(Object::toString)
        .collect(joining(","));

    logger.info("Now had enabled Plugins [{}], and started FileWatchListeners [{}]",
        enableAgentNames, listenerNames);
  }

  /**
   * custom class file transformer
   *
   * @author liuguangsheng
   * @since 1.0.0
   */
  static class CustomTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classfileBuffer) {
      if (StringUtil.isBlank(className)) {
        return null;
      }

      String newClassName = className.replace(SLASH, DOT);

      for (AgentService agentService : agentServices) {
        boolean checkedClass = agentService.checkAgentEnable(newClassName);
        if (agentService.isUseful() && !agentService.isInited()) {
          agentService.init();
          fileWatchService.registerListeners(agentService.getListeners());
          printAgentState();
        }

        // checkedClass表示当前加载的类newClassName是否有对应的MethodAdapter，当为false时，表示没有对应的MethodAdapter，
        // 这时候就直接跳过
        if (!checkedClass) {
          continue;
        }
        MethodAdapter adapter = agentService.getAdapterMap().get(newClassName);
        return adapter.transform();
      }

      return null;
    }
  }

}
