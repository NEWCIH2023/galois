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

package io.liuguangsheng.galois.service.spring.runners;

import io.liuguangsheng.galois.service.AgentService;
import io.liuguangsheng.galois.service.BeanReloader;
import io.liuguangsheng.galois.service.annotation.LazyBean;
import io.liuguangsheng.galois.service.monitor.ApacheFileWatchService;
import io.liuguangsheng.galois.service.monitor.FileChangedListener;
import io.liuguangsheng.galois.service.monitor.FileWatchService;
import io.liuguangsheng.galois.utils.ClassUtil;
import io.liuguangsheng.galois.utils.GaloisLog;
import org.slf4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static io.liuguangsheng.galois.constants.ClassNameConstant.PACKAGE_SERVICE;

/**
 * agent service init runner
 *
 * @author liuguangsheng
 */
public class AgentInitializeRunner extends AbstractRunner {

    private static final Logger logger = new GaloisLog(AgentInitializeRunner.class);
    private static final FileWatchService watchService = ApacheFileWatchService.getInstance();

    @Override
    public void started(ConfigurableApplicationContext context) {
        if (!canInvoke()) {
            return;
        }

        logger.info("{} Started with context {}.", getClass().getSimpleName(), context.getId());

        try {
            Set<Class<?>> lazyBeanFactorys = ClassUtil.scanAnnotationClass(PACKAGE_SERVICE, LazyBean.class);
            Set<AgentService> agentServices = ClassUtil.scanBaseClass(PACKAGE_SERVICE, AgentService.class)
                    .stream()
                    .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                    .map(clazz -> (AgentService) ClassUtil.getInstance(clazz))
                    .filter(Objects::nonNull)
                    .filter(AgentService::isSuitable)
                    .collect(Collectors.toSet());

            Map<String, Map.Entry<FileChangedListener, Integer>> tmpRankMap = new HashMap<>(64);

            for (AgentService agentService : agentServices) {
                for (Class<?> factory : lazyBeanFactorys) {
                    LazyBean lazyBean = factory.getAnnotation(LazyBean.class);
                    boolean byManager = lazyBean.manager().equals(agentService.getClass());

                    if (!ClassUtil.isClass(factory) || !byManager) {
                        continue;
                    }

                    if (BeanReloader.class.isAssignableFrom(factory)) {
                        BeanReloader<?> beanReloader = (BeanReloader<?>) ClassUtil.getInstance(factory);
                        Objects.requireNonNull(beanReloader,
                                "Get beanReloader instance <" + factory.getName() + "> " + "fail.");
                        if (beanReloader.isReady()) {
                            agentService.setBeanReloader(beanReloader);
                        }
                    } else if (FileChangedListener.class.isAssignableFrom(factory)) {
                        FileChangedListener listener = (FileChangedListener) ClassUtil.getInstance(factory);
                        agentService.registerFileChangedListener(listener);
                        tmpRankMap.put(lazyBean.value(), new AbstractMap.SimpleEntry<>(listener, lazyBean.rank()));
                    }

                }
            }

            tmpRankMap.values()
                    .stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .forEach(entry -> watchService.registerListener(entry.getKey()));

            watchService.start();
        } catch (Exception e) {
            logger.error("Invoke agentInitializeRunner failed.", e);
        }
    }
}
