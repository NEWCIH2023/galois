package org.newcih.service.agent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import org.newcih.util.GaloisLog;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

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

        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            AgentService.startAttach(pid);
        } catch (IOException e) {
            LOGGER.error("添加tools包到Bootstrap加载器发生异常", e);
            throw new RuntimeException(e);
        } catch (AgentLoadException | AttachNotSupportedException | AgentInitializationException e) {
            LOGGER.error("启动AgentService服务失败", e);
            throw new RuntimeException(e);
        }

        inst.addTransformer(new SpringTransformer(), true);
        inst.addTransformer(new MyBatisTransformer(), true);

        LOGGER.info("Premain服务执行完毕");
    }

}
