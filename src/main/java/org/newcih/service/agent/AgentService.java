package org.newcih.service.agent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.newcih.service.agent.frame.mybatis.MyBatisTransformer;
import org.newcih.service.agent.frame.spring.SpringTransformer;
import org.newcih.service.watch.ApacheFileWatchService;
import org.newcih.service.watch.frame.FileChangedListener;
import org.newcih.service.watch.frame.mybatis.MyBatisXmlListener;
import org.newcih.service.watch.frame.spring.SpringBeanListener;
import org.newcih.utils.GaloisLog;
import org.newcih.utils.SystemUtil;

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
    public static final GaloisLog LOGGER = GaloisLog.getLogger(AgentService.class);

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
        LOGGER.info("AgentService服务启动");

        // 添加类转换器
        inst.addTransformer(new SpringTransformer());
        inst.addTransformer(new MyBatisTransformer());

        // 创建文件类型监听器
        List<FileChangedListener> fileChangedListeners = Arrays.asList(
                new SpringBeanListener(inst),
                new MyBatisXmlListener()
        );
        String outputPath = SystemUtil.getOutputPath();
        LOGGER.info("Galois开始监听%s目录下文件变动", outputPath);

        ApacheFileWatchService watchService = new ApacheFileWatchService(outputPath, fileChangedListeners);
        watchService.start();

        try {
            LOGGER.debug("动态转换类开始");
            // 动态转换类
            inst.retransformClasses(
                    Class.forName(SpringTransformer.CLASS_PATH_BEAN_DEFINITION_SCANNER),
                    Class.forName(SpringTransformer.ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT),
                    Class.forName(MyBatisTransformer.SQL_SESSION_FACTORY_BEAN)
            );
        } catch (Exception e) {
            LOGGER.error("动态转换类发生异常", e);
        }
    }

    public static void attachProcess(String pid) throws AgentLoadException, IOException, AttachNotSupportedException, AgentInitializationException {
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
        LOGGER.info("AgentService将绑定到pid为[%s]的java进程中", pid);

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

    public static void main(String[] args) throws AgentLoadException, IOException, AttachNotSupportedException, AgentInitializationException {
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
