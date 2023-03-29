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

package org.newcih.galois.service.runners;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.newcih.galois.service.AgentService;
import org.newcih.galois.service.FileChangedListener;
import org.newcih.galois.service.FileWatchService;
import org.newcih.galois.service.annotation.LazyBean;
import org.newcih.galois.utils.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import static org.newcih.galois.constants.ClassNameConstant.SERVICE_PACKAGE;
import static org.newcih.galois.constants.Constant.GET_INSTANCE;

/**
 * agent service init runner
 *
 * @author liuguangsheng
 */
public class AgentInitializeRunner extends AbstractRunner {

    private static final FileWatchService fileWatchService = FileWatchService.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(AgentInitializeRunner.class);
    private final Collection<AgentService> agentServices = new ArrayList<>(32);

    /**
     * Instantiates a new Agent service init runner.
     */
    public AgentInitializeRunner() {
        setRank(FileWatchRunner.RANK + 1);
    }

    /**
     * Add agent service.
     *
     * @param agentService the agent service
     */
    public void addAgentService(AgentService agentService) {
        this.agentServices.add(agentService);
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        try {
            for (AgentService agentService : agentServices) {
                if (!agentService.isUseful()) {
                    continue;
                }

                Set<Class<?>> fileChangedClasses = ClassUtil.scanBaseClass(SERVICE_PACKAGE, FileChangedListener.class);
                for (Class<?> fileChangedClass : fileChangedClasses) {
                    if (Modifier.isInterface(fileChangedClass.getModifiers())) {
                        continue;
                    }

                    FileChangedListener listener = (FileChangedListener) fileChangedClass.newInstance();
                    LazyBean lazyBean = fileChangedClass.getAnnotation(LazyBean.class);


                }
            }
        } catch (Exception e) {

        }
    }
}
