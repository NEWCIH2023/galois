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

package io.liuguangsheng.galois.service;

import io.liuguangsheng.galois.service.annotation.AsmVisitor;
import io.liuguangsheng.galois.service.spring.BannerService;
import io.liuguangsheng.galois.service.spring.runners.AbstractRunner;
import io.liuguangsheng.galois.service.spring.runners.SpringRunnerManager;
import io.liuguangsheng.galois.utils.ClassUtil;
import io.liuguangsheng.galois.utils.GaloisLog;
import io.liuguangsheng.galois.utils.StringUtil;
import org.slf4j.Logger;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.liuguangsheng.galois.constants.ClassNameConstant.PACKAGE_SERVICE;
import static io.liuguangsheng.galois.constants.Constant.COMMA;
import static io.liuguangsheng.galois.constants.Constant.DOT;
import static io.liuguangsheng.galois.constants.Constant.SLASH;

/**
 * premain agent服务入口
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class PremainService {

    private static final Logger logger = new GaloisLog(PremainService.class);
    private static final Map<String, AgentService> agentServiceMap = new HashMap<>(8);
    private static final SpringRunnerManager runManager = SpringRunnerManager.getInstance();

    static {
        scanAgentService();
        scanAsmVisitor();
        scanRunner();

        logger.debug("Scan {} agentServices as list [{}].", agentServiceMap.keySet().size(), agentServiceMap.values()
                .stream()
                .map(AgentService::toString)
                .collect(Collectors.joining(COMMA)));
    }

    /**
     * premain entry
     *
     * @param agentArgs agent args
     * @param inst      instrument object
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        if (inst == null) {
            logger.error("Your program do not support instrumentation, Please remove this javaagent.");
            System.exit(0);
        }

        try {
            inst.addTransformer(new CustomTransformer(), true);
            ClassUtil.setInstrumentation(inst);
//            BannerService.printBanner();
        } catch (Throwable e) {
            logger.error("Start Premain Service fail.", e);
        }
    }

    /**
     * scan agent service
     */
    private static void scanAgentService() {
        ClassUtil.scanBaseClass(PACKAGE_SERVICE, AgentService.class)
                .stream()
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .forEach(clazz -> {
                    Object agentService = ClassUtil.getInstance(clazz);
                    if (agentService != null) {
                        agentServiceMap.put(clazz.getName(), (AgentService) agentService);
                    }
                });
    }

    /**
     * scan asm visitor
     */
    private static void scanAsmVisitor() {
        ClassUtil.scanBaseClass(PACKAGE_SERVICE, MethodAdapter.class)
                .stream()
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .forEach(clazz -> {
                    MethodAdapter adapter = (MethodAdapter) ClassUtil.getInstance(clazz);
                    AsmVisitor visitor = clazz.getAnnotation(AsmVisitor.class);
                    if (visitor == null) {
                        return;
                    }

                    AgentService service = (AgentService) ClassUtil.getInstance(visitor.manager());
                    Objects.requireNonNull(service, "Get agentService instance <" + visitor.manager() + "> by " +
                                    "visitor annotation failed.")
                            .registerMethodAdapter(adapter);
                });
    }

    /**
     * scan runner
     */
    private static void scanRunner() {
        ClassUtil.scanBaseClass(PACKAGE_SERVICE, AbstractRunner.class)
                .stream()
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .map(ClassUtil::getInstance)
                .filter(Objects::nonNull)
                .map(obj -> (AbstractRunner) obj)
                .forEach(runManager::addRunner);
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

            String fullClassName = className.replace(SLASH, DOT);
            Collection<AgentService> agentServices = agentServiceMap.values();

            for (AgentService agentService : agentServices) {
                boolean isNecessaryClass = agentService.isNecessaryClass(fullClassName);
                // checkedClass表示当前加载的类newClassName是否有对应的MethodAdapter，当为false时，
                // 表示没有对应的MethodAdapter，这时候就直接跳过
                if (!isNecessaryClass) {
                    continue;
                }

                MethodAdapter adapter = agentService.getMethodAdapterMap().get(fullClassName);
                return adapter.transform(classfileBuffer);
            }

            return null;
        }
    }
}
