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

package org.newcih.galois.service.agent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.newcih.galois.service.watch.ApacheFileWatchService;
import org.newcih.galois.service.watch.frame.FileChangedListener;
import org.newcih.galois.service.watch.frame.mybatis.MyBatisXmlListener;
import org.newcih.galois.service.watch.frame.spring.SpringBeanListener;
import org.newcih.galois.utils.GaloisLog;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * AgentMain服务类
 * <p>
 * 用于动态调试使用
 */
public class AgentService {
    public static final GaloisLog logger = GaloisLog.getLogger(AgentService.class);

    public static final String SUN_JVM_ARGS = "sun.jvm.args";
    public static final String AGENT_JAR_NAME = "galois-jar-with-dependencies.jar";
    public static final String JAVA_AGENT_PREFIX = "-javaagent:";

    /**
     * AgentMain入口
     *
     * @param agentArgs
     * @param inst
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        logger.info("AgentService服务启动");

        // 添加类转换器
//        inst.addTransformer(new SpringTransformer());
//        inst.addTransformer(new MyBatisTransformer());

        // 创建文件类型监听器
        List<FileChangedListener> fileChangedListeners = Arrays.asList(
                new SpringBeanListener(inst),
                new MyBatisXmlListener()
        );
        String outputPath = "";
        logger.info("Galois开始监听{}目录下文件变动", outputPath);

        ApacheFileWatchService watchService = new ApacheFileWatchService(outputPath, fileChangedListeners);
        watchService.start();

        try {
            logger.debug("动态转换类开始");
            // 动态转换类
//            inst.retransformClasses(
//                    Class.forName(SpringTransformer.CLASS_PATH_BEAN_DEFINITION_SCANNER),
//                    Class.forName(SpringTransformer.ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT),
//                    Class.forName(MyBatisTransformer.SQL_SESSION_FACTORY_BEAN)
//            );
        } catch (Exception e) {
            logger.error("动态转换类发生异常", e);
        }
    }

    public static void attachProcess(String pid) throws AgentLoadException, IOException, AttachNotSupportedException,
            AgentInitializationException {
        attachProcess(pid, null);
    }

    /**
     * 使该AgentServcie关联到启动的java项目pid
     *
     * @param pid
     * @throws IOException
     * @throws AttachNotSupportedException
     * @throws AgentLoadException
     * @throws AgentInitializationException
     */
    public static void attachProcess(String pid, String agentPath) throws IOException, AttachNotSupportedException,
            AgentLoadException, AgentInitializationException {
        logger.info("AgentService将绑定到pid为[{}]的java进程中", pid);

        VirtualMachine vm = VirtualMachine.attach(pid);
        if (agentPath == null || agentPath.trim().isEmpty()) {
            // 要求通过 -javaagent的方式添加该jar，因此可以通过javaagent参数获取到该jar包位置，从而动态loadAgent
            String args = vm.getAgentProperties().getProperty(SUN_JVM_ARGS);
            agentPath = Stream.of(args.split(" "))
                    .filter(str -> str.endsWith(AGENT_JAR_NAME))
                    .findFirst()
                    .orElseThrow(() -> new NullPointerException("无法定位" + AGENT_JAR_NAME + "位置"))
                    .replace(JAVA_AGENT_PREFIX, "");
        }

        vm.loadAgent(agentPath);
        vm.detach();
    }

    public static void main(String[] args) throws AgentLoadException, IOException, AttachNotSupportedException,
            AgentInitializationException {
        final String pidPrefix = "-pid:";
        final String agentJarPathPrefix = "-agentpath:";
        String pid = "", agentPath = "";

        for (String arg : args) {
            if (arg.contains(pidPrefix)) {
                pid = arg.replace(pidPrefix, "");
            } else if (arg.contains(agentJarPathPrefix)) {
                agentPath = arg.replace(agentJarPathPrefix, "");
            }
        }

        if (pid.isEmpty() || agentPath.isEmpty()) {
            throw new NullPointerException("pid or agentpath is empty，can't attach java process!");
        }

        AgentService.attachProcess(pid, agentPath);
    }
}
