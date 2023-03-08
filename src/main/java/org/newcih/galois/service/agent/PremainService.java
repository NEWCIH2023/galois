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

import org.newcih.galois.service.ApacheFileWatchService;
import org.newcih.galois.service.BannerService;
import org.newcih.galois.service.ProjectFileManager;
import org.newcih.galois.service.agent.frame.corm.CormAgentService;
import org.newcih.galois.service.agent.frame.mybatis.MyBatisAgentService;
import org.newcih.galois.service.agent.frame.spring.SpringAgentService;
import org.newcih.galois.utils.GaloisLog;

import java.lang.instrument.Instrumentation;
import java.util.*;

/**
 *
 */
public class PremainService {

    public static final GaloisLog logger = GaloisLog.getLogger(PremainService.class);
    private static final Map<String, MethodAdapter> mac = new HashMap<>(64);
    private static final List<FileChangedListener> listeners = new ArrayList<>(16);
    private static final ProjectFileManager fileManager = ProjectFileManager.getInstance();
    private static Instrumentation instrumentation;
    // adding new agent service here
    private static final List<AgentService> agentServices = Arrays.asList(
            SpringAgentService.getInstance(),
            MyBatisAgentService.getInstance(),
            CormAgentService.getInstance()
    );

    /**
     * premain entry
     *
     * @param agentArgs
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        if (instrumentation != null) {
            return;
        }
        instrumentation = inst;

        BannerService.printBanner();

        registerAgent();

        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (className == null || className.trim().length() == 0) {
                return null;
            }

            String newClassName = className.replace("/", ".");
            MethodAdapter methodAdapter = mac.get(newClassName);
            if (methodAdapter != null && methodAdapter.isUseful()) {
                return methodAdapter.transform();
            }

            return null;
        });

        // start file change listener service
        String sourcePath = fileManager.getSourcePath();
        logger.info("begin listen file change in path [{}]", sourcePath);

        new ApacheFileWatchService(sourcePath, listeners).start();
    }

    /**
     * register agent service
     */
    private static void registerAgent() {
        try {

            for (AgentService agentService : agentServices) {
                if (agentService != null) {
                    agentService.installAgentService(mac);
                    listeners.addAll(agentService.getListener());
                }
            }
        } catch (Exception e) {
            logger.error("register agent service failed", e);
        }
    }
}
