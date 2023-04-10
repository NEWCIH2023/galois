/*
 * MIT License
 *
 * Copyright (c) [2023] [$user]
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

package io.liuguangsheng.galois.service.runners;

import io.liuguangsheng.galois.constants.ClassNameConstant;
import io.liuguangsheng.galois.service.AgentService;
import io.liuguangsheng.galois.service.BeanReloader;
import io.liuguangsheng.galois.service.annotation.LazyBean;
import io.liuguangsheng.galois.service.monitor.FileChangedListener;
import io.liuguangsheng.galois.service.monitor.FileWatchService;
import io.liuguangsheng.galois.utils.ClassUtil;
import io.liuguangsheng.galois.utils.GaloisLog;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * agent service init runner
 *
 * @author liuguangsheng
 */
public class AgentInitializeRunner extends AbstractRunner {

    private static final FileWatchService fileWatchService = FileWatchService.getInstance();
    private static final Logger logger = new GaloisLog(AgentInitializeRunner.class);

    /**
     * Instantiates a new Agent service init runner.
     */
    public AgentInitializeRunner() {
        setRank(0);
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        if (!isCanInvoke()) {
            return;
        }

        logger.info("{} with context {} is {}", getClass().getSimpleName(), context.getId(), "started");

        try {
            Set<Class<?>> lazyBeanFactorys = ClassUtil.scanAnnotationClass(ClassNameConstant.SERVICE_PACKAGE,
                    LazyBean.class);
            Set<AgentService> agentServices =
                    ClassUtil.scanBaseClass(ClassNameConstant.SERVICE_PACKAGE, AgentService.class).stream().filter(clazz -> !Modifier.isAbstract(clazz.getModifiers())).map(clazz -> (AgentService) ClassUtil.getInstance(clazz)).collect(Collectors.toSet());

            for (AgentService agentService : agentServices) {
                if (!agentService.isUseful()) {
                    continue;
                }

                for (Class<?> lazyBeanFactory : lazyBeanFactorys) {
                    int modifiers = lazyBeanFactory.getModifiers();
                    LazyBean lazyBean = lazyBeanFactory.getAnnotation(LazyBean.class);
                    boolean isManager = lazyBean.manager().equals(agentService.getClass());

                    if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers) || !isManager) {
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

            fileWatchService.start();
        } catch (Exception ignored) {
        }
    }
}
