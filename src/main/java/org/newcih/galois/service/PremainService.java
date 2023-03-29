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

package org.newcih.galois.service;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.newcih.galois.service.runners.AgentInitializeRunner;
import org.newcih.galois.utils.ClassUtil;
import org.newcih.galois.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.newcih.galois.constants.ClassNameConstant.SERVICE_PACKAGE;
import static org.newcih.galois.constants.Constant.COMMA;
import static org.newcih.galois.constants.Constant.DOT;
import static org.newcih.galois.constants.Constant.GET_INSTANCE;
import static org.newcih.galois.constants.Constant.SLASH;

/**
 * premain agent服务入口
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class PremainService {

    private static final Logger logger = LoggerFactory.getLogger(PremainService.class);
    private static final Map<String, AgentService> agentServiceMap = new HashMap<>(8);
    private static final AgentInitializeRunner initRunner = new AgentInitializeRunner();
    private static final SpringRunnerManager runManager = SpringRunnerManager.getInstance();

    static {
        // scan agent service over abstract class named AgentService
        try {
            Set<Class<?>> agentClasses = ClassUtil.scanBaseClass(SERVICE_PACKAGE,
                    Collections.singletonList(AgentService.class));
            logger.debug("Find agent class as list [{}].", agentClasses);

            for (Class<?> agentClass : agentClasses) {
                if (Modifier.isAbstract(agentClass.getModifiers())) {
                    continue;
                }

                Method getInstanceMethod = agentClass.getMethod(GET_INSTANCE);
                AgentService agentService = (AgentService) getInstanceMethod.invoke(null);
                agentServiceMap.put(agentClass.getName(), agentService);
                initRunner.addAgentService(agentService);
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        logger.info("Register {} agentServices as list [{}].", agentServiceMap.keySet().size(),
                agentServiceMap.values().stream()
                        .map(AgentService::toString)
                        .collect(Collectors.joining(COMMA)));

        // register agent service initializer runner
        runManager.addRunner(initRunner);
    }

    /**
     * premain entry
     *
     * @param agentArgs agent args
     * @param inst      instrument object
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        if (inst == null) {
            logger.error("Your program do not support instrumentation.");
            System.exit(0);
        }

        try {
            ClassFileTransformer custom = new CustomTransformer();
            inst.addTransformer(custom, true);
            ClassUtil.setInstrumentation(inst);
            BannerService.printBanner();
        } catch (Throwable e) {
            logger.error("Start Premain Service fail.", e);
        }
    }

    /**
     * custom class file transformer
     *
     * @author liuguangsheng
     */
    static class CustomTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            if (StringUtil.isBlank(className)) {
                return null;
            }

            String newClassName = className.replace(SLASH, DOT);
            Collection<AgentService> agentServices = agentServiceMap.values();

            for (AgentService agentService : agentServices) {
                boolean isNecessaryClass = agentService.checkNecessaryClass(newClassName);
                // checkedClass表示当前加载的类newClassName是否有对应的MethodAdapter，当为false时，
                // 表示没有对应的MethodAdapter，这时候就直接跳过
                if (!isNecessaryClass) {
                    continue;
                }

                MethodAdapter adapter = agentService.getMethodAdapterMap().get(newClassName);
                return adapter.transform();
            }

            return null;
        }
    }
}
