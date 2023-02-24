package org.newcih.service.agent;

import org.newcih.service.agent.frame.mybatis.MyBatisTransformer;
import org.newcih.service.agent.frame.spring.SpringTransformer;
import org.newcih.service.watch.ApacheFileWatchService;
import org.newcih.service.watch.frame.FileChangedListener;
import org.newcih.service.watch.frame.mybatis.MyBatisXmlListener;
import org.newcih.service.watch.frame.spring.SpringBeanListener;
import org.newcih.utils.GaloisLog;
import org.newcih.utils.SystemUtil;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

/**
 * PreMain服务类
 */
public class PremainService {

    public static final GaloisLog LOGGER = GaloisLog.getLogger(PremainService.class);

    /**
     * Premain入口
     *
     * @param agentArgs
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        LOGGER.info("PremainService服务启动");

        // 添加类转换器
        inst.addTransformer(new SpringTransformer());
        inst.addTransformer(new MyBatisTransformer());

        // 启用文件变更监听服务
        List<FileChangedListener> fileChangedListeners = Arrays.asList(
                new SpringBeanListener(inst),
                new MyBatisXmlListener()
        );
        String outputPath = SystemUtil.getOutputPath();
        LOGGER.info("Galois开始监听%s目录下文件变动", outputPath);

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
        LOGGER.info("PremainService服务启动");

        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            AgentService.attachProcess(pid);
        } catch (Exception e) {
            LOGGER.error("启动AgentService服务失败", e);
            throw new RuntimeException(e);
        }

        inst.addTransformer(new SpringTransformer(), true);
        inst.addTransformer(new MyBatisTransformer(), true);

        LOGGER.info("Premain服务执行完毕");
    }

}
