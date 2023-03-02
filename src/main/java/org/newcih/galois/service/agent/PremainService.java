package org.newcih.galois.service.agent;

import org.newcih.galois.service.agent.frame.mybatis.SqlSessionFactoryBeanVisitor;
import org.newcih.galois.service.agent.frame.spring.ApplicationContextVisitor;
import org.newcih.galois.service.agent.frame.spring.BeanDefinitionScannerVisitor;
import org.newcih.galois.service.watch.ApacheFileWatchService;
import org.newcih.galois.service.watch.ProjectFileManager;
import org.newcih.galois.service.watch.frame.FileChangedListener;
import org.newcih.galois.service.watch.frame.mybatis.MyBatisXmlListener;
import org.newcih.galois.service.watch.frame.spring.SpringBeanListener;
import org.newcih.galois.utils.GaloisLog;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PreMain服务类
 */
public class PremainService {

    public static final GaloisLog logger = GaloisLog.getLogger(PremainService.class);
    private static final Map<String, MethodAdapter> mac = new HashMap<>(64);
    private static final ProjectFileManager fileManager = ProjectFileManager.getInstance();
    private static Instrumentation instrumentation;

    static {
        Arrays.asList(
                new ApplicationContextVisitor(),
                new BeanDefinitionScannerVisitor(),
                new SqlSessionFactoryBeanVisitor()
        ).forEach(visit -> visit.install(mac));
    }

    /**
     * Premain入口
     *
     * @param agentArgs
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("premainservice service started !");

        if (instrumentation != null) {
            return;
        }
        instrumentation = inst;

        // 添加类转换器
        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (className == null || className.trim().length() == 0) {
                return null;
            }

            String newClassName = className.replace("/", ".");
            MethodAdapter methodAdapter = mac.get(newClassName);
            if (methodAdapter != null) {
                return methodAdapter.transform();
            }

            return null;
        });

        // start file change listener service
        List<FileChangedListener> fileChangedListeners = Arrays.asList(
                new SpringBeanListener(getInstrumentation()),
                new MyBatisXmlListener()
        );

        String outputPath = fileManager.getOutputPath();
        logger.info("begin listen file change in path [{}]", outputPath);

        Thread daemonFileListener = new Thread(() -> {
            ApacheFileWatchService watchService = new ApacheFileWatchService(outputPath, fileChangedListeners);
            watchService.start();
        });
        daemonFileListener.setDaemon(true);
        daemonFileListener.start();
    }

    public static Instrumentation getInstrumentation() {
        if (instrumentation == null) {
            throw new NullPointerException("no instrumentation object found!");
        }

        return instrumentation;
    }

    /**
     * Premain入口
     *
     * @param agentArgs
     * @param inst
     */
    @Deprecated
    public static void oldpremain(String agentArgs, Instrumentation inst) {
        logger.info("premainservice service started !");

        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            AgentService.attachProcess(pid);
        } catch (Exception e) {
            logger.error("agentservice start failed", e);
            throw new RuntimeException(e);
        }

        logger.info("premain service execute complete");
    }

}
