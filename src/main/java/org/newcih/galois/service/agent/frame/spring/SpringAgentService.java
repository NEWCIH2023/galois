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

package org.newcih.galois.service.agent.frame.spring;

import org.newcih.galois.service.agent.AgentService;
import org.newcih.galois.service.agent.BeanReloader;
import org.newcih.galois.service.agent.FileChangedListener;
import org.newcih.galois.service.agent.MethodAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.newcih.galois.constants.ClassNameConstant.ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT;
import static org.newcih.galois.constants.ClassNameConstant.CLASS_PATH_BEAN_DEFINITION_SCANNER;

public class SpringAgentService extends AgentService {

    private static SpringAgentService springAgent;

    private SpringAgentService(List<FileChangedListener> listener, BeanReloader<?> beanReloader, Map<String,
            MethodAdapter> classNameToMethodMap) {
        super(listener, beanReloader, classNameToMethodMap);
    }

    public static SpringAgentService getInstance() {
        if (springAgent != null) {
            return springAgent;
        }

        Map<String, MethodAdapter> methodAdapterMap = new HashMap<>(8);
        methodAdapterMap.put(CLASS_PATH_BEAN_DEFINITION_SCANNER, new BeanDefinitionScannerVisitor());
        methodAdapterMap.put(ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT,
                new ApplicationContextVisitor());

        springAgent = new SpringAgentService(Collections.singletonList(new SpringBeanListener()),
                SpringBeanReloader.getInstance(), methodAdapterMap);
        return springAgent;
    }

    @Override
    public boolean isUseful() {
        try {
            Class.forName(CLASS_PATH_BEAN_DEFINITION_SCANNER);
            Class.forName(ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT);
        } catch (ClassNotFoundException e) {
            return false;
        }

        return true;
    }
}
