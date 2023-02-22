package org.newcih.service.agent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.newcih.service.watch.ApacheFileWatchService;
import org.newcih.service.watch.frame.FileChangedListener;
import org.newcih.service.watch.frame.MyBatisXmlListener;
import org.newcih.service.watch.frame.SpringBeanListener;
import org.newcih.util.GaloisLog;
import org.newcih.util.SystemUtils;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AgentService {
    public static final GaloisLog LOGGER = GaloisLog.getLogger(AgentService.class);

    public static final boolean useMaven = true;

    public static final String SUN_JVM_ARGS = "sun.jvm.args";

    public static final String AGENT_JAR_NAME = "galois-jar-with-dependencies.jar";

    public static final List<FileChangedListener> fileChangedListeners = new ArrayList<>(32);

    /**
     * AgentMain入口
     *
     * @param agentArgs
     * @param inst
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        LOGGER.info("AgentService服务启动");

        // 创建文件类型监听器
        new SpringBeanListener();
        new MyBatisXmlListener();

        String outputPath = SystemUtils.getOutputPath(inst, useMaven);
        ApacheFileWatchService watchService = new ApacheFileWatchService(outputPath);

        watchService.setCreateHandler(file -> fileChangedListeners.stream()
                .filter(listener -> listener.validFile(file))
                .forEach(listener -> listener.fileCreatedHandle(file, inst)));

        watchService.setDeleteHandler(file -> fileChangedListeners.stream()
                .filter(listener -> listener.validFile(file))
                .forEach(listener -> listener.fileDeletedHandle(file, inst)));

        watchService.setModiferHandler(file -> fileChangedListeners.stream()
                .filter(listener -> listener.validFile(file))
                .forEach(listener -> listener.fileModifiedHandle(file, inst)));

        watchService.start();
        LOGGER.info("已启动AgentService的文件监控服务");
    }

    public static void startAttach(String pid) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        LOGGER.info("AgentService将绑定到pid为[%s]的java进程中", pid);
        VirtualMachine vm = VirtualMachine.attach(pid);
        // 要求通过 -javaagent的方式添加该jar，因此可以通过javaagent参数获取到该jar包位置，从而动态loadAgent
        String args = vm.getAgentProperties().getProperty(SUN_JVM_ARGS);
        String agentJarPath = Stream.of(args.split(" "))
                .filter(str -> str.endsWith(AGENT_JAR_NAME))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("无法定位" + AGENT_JAR_NAME + "位置"))
                .replace("-javaagent:", "");
        vm.loadAgent(agentJarPath);
        vm.detach();
    }
}
