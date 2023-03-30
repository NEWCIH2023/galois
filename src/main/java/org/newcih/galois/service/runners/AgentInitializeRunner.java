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

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;
import org.newcih.galois.service.AgentService;
import org.newcih.galois.service.BeanReloader;
import org.newcih.galois.service.FileChangedListener;
import org.newcih.galois.service.FileWatchService;
import org.newcih.galois.service.annotation.LazyBean;
import org.newcih.galois.utils.ClassUtil;
import org.newcih.galois.utils.GaloisLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import static org.newcih.galois.constants.ClassNameConstant.SERVICE_PACKAGE;

/**
 * agent service init runner
 *
 * @author liuguangsheng
 */
public class AgentInitializeRunner extends AbstractRunner {

    private static final FileWatchService fileWatchService = FileWatchService.getInstance();
    private static final Logger logger = GaloisLog.g

    /**
     * Instantiates a new Agent service init runner.
     */
    public AgentInitializeRunner() {
        setRank(FileWatchRunner.RANK + 1);
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        logger.info("{} is Running.", getClass().getSimpleName());

        try {
            Set<Class<?>> lazyBeanFactorys = ClassUtil.scanAnnotationClass(SERVICE_PACKAGE, LazyBean.class);
            Set<AgentService> agentServices = ClassUtil.scanBaseClass(SERVICE_PACKAGE, AgentService.class).stream()
                    .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                    .map(clazz -> (AgentService) ClassUtil.getInstance(clazz))
                    .collect(Collectors.toSet());

            for (AgentService agentService : agentServices) {
                if (!agentService.isUseful()) {
                    logger.info("{} wasn't enable, do not init it.", agentService);
                    continue;
                }

                for (Class<?> lazyBeanFactory : lazyBeanFactorys) {
                    int modifiers = lazyBeanFactory.getModifiers();
                    LazyBean lazyBean = lazyBeanFactory.getAnnotation(LazyBean.class);
                    if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers) || !lazyBean.manager().equals(agentService.getClass())) {
                        continue;
                    }

                    if (BeanReloader.class.isAssignableFrom(lazyBeanFactory)) {
                        BeanReloader<?> beanReloader = (BeanReloader<?>) ClassUtil.getInstance(lazyBeanFactory);
                        agentService.setBeanReloader(beanReloader);
                    } else if (FileChangedListener.class.isAssignableFrom(lazyBeanFactory)) {
                        FileChangedListener listener = (FileChangedListener) ClassUtil.getInstance(lazyBeanFactory);
                        agentService.registerFileChangedListener(listener);
                        fileWatchService.registerListener(listener);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
