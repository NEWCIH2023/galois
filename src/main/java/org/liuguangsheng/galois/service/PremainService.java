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

package org.liuguangsheng.galois.service;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.liuguangsheng.galois.service.annotation.AsmVisitor;
import org.liuguangsheng.galois.service.runners.SpringRunnerManager;
import org.liuguangsheng.galois.utils.ClassUtil;
import org.liuguangsheng.galois.utils.StringUtil;
import org.liuguangsheng.galois.service.runners.AbstractRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.liuguangsheng.galois.constants.ClassNameConstant.SERVICE_PACKAGE;
import static org.liuguangsheng.galois.constants.Constant.COMMA;
import static org.liuguangsheng.galois.constants.Constant.DOT;
import static org.liuguangsheng.galois.constants.Constant.SLASH;

/**
 * premain agent服务入口
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class PremainService {

    private static final Logger logger = LoggerFactory.getLogger(PremainService.class);
    private static final Map<String, AgentService> agentServiceMap = new HashMap<>(8);
    private static final SpringRunnerManager runManager = SpringRunnerManager.getInstance();

    static {
        scanAgentService();
        scanAsmVisitor();
        scanRunner();

        logger.info("Scan {} agentServices as list [{}].", agentServiceMap.keySet().size(),
                agentServiceMap.values().stream().map(AgentService::toString).collect(Collectors.joining(COMMA)));
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

    private static void scanAgentService() {
        // scan agent service over abstract class named AgentService
        Set<Class<?>> agentClasses = ClassUtil.scanBaseClass(SERVICE_PACKAGE, AgentService.class);

        for (Class<?> agentClass : agentClasses) {
            if (Modifier.isAbstract(agentClass.getModifiers())) {
                continue;
            }

            Optional.ofNullable(ClassUtil.getInstance(agentClass)).ifPresent(object -> {
                AgentService agentService = (AgentService) object;
                agentServiceMap.put(agentClass.getName(), agentService);
            });
        }
    }

    private static void scanAsmVisitor() {
        Set<Class<?>> visitorClasses = ClassUtil.scanBaseClass(SERVICE_PACKAGE, MethodAdapter.class);
        for (Class<?> visitorClass : visitorClasses) {
            if (Modifier.isAbstract(visitorClass.getModifiers())) {
                continue;
            }

            MethodAdapter methodAdapter = (MethodAdapter) ClassUtil.getInstance(visitorClass);
            AsmVisitor visitor = visitorClass.getAnnotation(AsmVisitor.class);

            Optional.ofNullable(ClassUtil.getInstance(visitor.manager())).ifPresent(object -> ((AgentService) object).registerMethodAdapter(methodAdapter));
        }
    }

    private static void scanRunner() {
        Set<Class<?>> runnerClasses = ClassUtil.scanBaseClass(SERVICE_PACKAGE, AbstractRunner.class);
        for (Class<?> runnerClass : runnerClasses) {
            if (Modifier.isAbstract(runnerClass.getModifiers())) {
                continue;
            }

            Optional.ofNullable(ClassUtil.getInstance(runnerClass))
                    .ifPresent(object -> runManager.addRunner((AbstractRunner) object));
        }

    }
}
