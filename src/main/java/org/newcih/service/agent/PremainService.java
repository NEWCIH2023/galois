package org.newcih.service.agent;

import org.newcih.service.agent.frame.mybatis.SqlSessionFactoryBeanVisitor;
import org.newcih.service.agent.frame.spring.ApplicationContextVisitor;
import org.newcih.service.agent.frame.spring.BeanDefinitionScannerVisitor;
import org.newcih.service.watch.ApacheFileWatchService;
import org.newcih.service.watch.ProjectFileManager;
import org.newcih.service.watch.frame.FileChangedListener;
import org.newcih.service.watch.frame.mybatis.MyBatisXmlListener;
import org.newcih.service.watch.frame.spring.SpringBeanListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final Logger logger = LoggerFactory.getLogger(PremainService.class);
    private static final Map<String, MethodAdapter> mac = new HashMap<>(64);
    private static final ProjectFileManager fileManager = ProjectFileManager.getInstance();

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
                new SpringBeanListener(inst),
                new MyBatisXmlListener()
        );

        String outputPath = fileManager.getOutputPath();
        logger.info("begin listen file change in path [{}]", outputPath);

        ApacheFileWatchService watchService = new ApacheFileWatchService(outputPath, fileChangedListeners);
        watchService.start();
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
