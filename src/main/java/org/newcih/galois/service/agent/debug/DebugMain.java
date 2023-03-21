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

package org.newcih.galois.service.agent.debug;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import org.newcih.galois.service.agent.AgentService;
import org.newcih.galois.service.agent.PremainService;
import org.newcih.galois.utils.GaloisLog;

/**
 * 支持通过Attach Agent方式动态接入AgentService
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class DebugMain {

  public static final GaloisLog logger = GaloisLog.getLogger(DebugMain.class);

  public static void main(String[] args)
      throws IOException, AttachNotSupportedException, AgentLoadException,
      AgentInitializationException {

    System.out.println(args);
    String pid = args[0];
    logger.info("将绑定到pid为[{}]的java进程中", pid);

    VirtualMachine vm = VirtualMachine.attach(pid);
    // 要求通过 -javaagent的方式添加该jar，因此可以通过javaagent参数获取到该jar包位置，从而动态loadAgent
    vm.loadAgent("./galois.jar");
    vm.detach();

    logger.info("已绑定到pid[{}]的java进程中", pid);
  }

  /**
   * 动态Attach Agent的入口
   *
   * @param agentArgs
   * @param inst
   */
  public static void agentmain(String agentArgs, Instrumentation inst)
      throws ClassNotFoundException,
      UnmodifiableClassException {
    PremainService.premain(agentArgs, inst);
    logger.info("执行premain方法完成");

    for (AgentService agentService : PremainService.agentServices) {
      for (String necessaryClass : agentService.getNecessaryClasses()) {
        inst.retransformClasses(Class.forName(necessaryClass));
      }
    }

    logger.info("执行agentmain方法完成");
  }

}
