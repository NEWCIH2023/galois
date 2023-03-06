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

import org.newcih.galois.service.BannerService;
import org.newcih.galois.service.ProjectFileManager;
import org.newcih.galois.service.agent.frame.mybatis.SqlSessionFactoryBeanVisitor;
import org.newcih.galois.service.agent.frame.spring.ApplicationContextVisitor;
import org.newcih.galois.service.agent.frame.spring.BeanDefinitionScannerVisitor;
import org.newcih.galois.service.watch.ApacheFileWatchService;
import org.newcih.galois.service.watch.frame.FileChangedListener;
import org.newcih.galois.service.watch.frame.mybatis.MyBatisXmlListener;
import org.newcih.galois.service.watch.frame.spring.SpringBeanListener;
import org.newcih.galois.utils.GaloisLog;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PreMain服务类
 */
public class PremainService {

    public static final GaloisLog logger = GaloisLog.getLogger(PremainService.class);
    private static final Map<String, MethodAdapter> mac = new HashMap<>(64);
    private static final ProjectFileManager fileManager = ProjectFileManager.getInstance();
    private static Instrumentation instrumentation;
    private static final List<MethodAdapter> methodAdapters = Arrays.asList(
            new ApplicationContextVisitor(),
            new BeanDefinitionScannerVisitor(),
            new SqlSessionFactoryBeanVisitor()
    );

    /**
     * Premain入口
     *
     * @param agentArgs
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        if (instrumentation != null) {
            return;
        }
        instrumentation = inst;

        // print galois banner
        BannerService.printBanner();

        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (className == null || className.trim().length() == 0) {
                return null;
            }

            String newClassName = className.replace("/", ".");
            MethodAdapter methodAdapter = mac.get(newClassName);
            if (methodAdapter != null && methodAdapter.usable()) {
                return methodAdapter.transform();
            }

            return null;
        });

        // start file change listener service
        List<FileChangedListener> fileChangedListeners = Arrays.asList(
                new SpringBeanListener(inst),
                new MyBatisXmlListener()
        );

        String sourcePath = fileManager.getSourcePath();
        logger.info("begin listen file change in path [{}]", sourcePath);

        new ApacheFileWatchService(sourcePath, fileChangedListeners).start();
    }

}
