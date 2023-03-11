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

package org.newcih.galois.service.agent.frame.corm;

import org.newcih.galois.service.agent.AgentService;
import org.newcih.galois.service.agent.BeanReloader;
import org.newcih.galois.service.agent.FileChangedListener;
import org.newcih.galois.service.agent.MethodAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.newcih.galois.constants.ClassNameConstant.COMTOP_SQL_SESSION_FACTORY;

public class CormAgentService extends AgentService {

    private static CormAgentService cormAgentService;

    private CormAgentService(List<FileChangedListener> listener, BeanReloader<?> beanReloader, Map<String,
            MethodAdapter> classNameToMethodMap) {
        super(listener, beanReloader, classNameToMethodMap);
    }

    public static CormAgentService getInstance() {
        if (cormAgentService != null) {
            return cormAgentService;
        }

        Map<String, MethodAdapter> methodAdapterMap = new HashMap<>(8);
        methodAdapterMap.put(COMTOP_SQL_SESSION_FACTORY, new ComtopSqlSessionFactoryBeanVisitor());
        cormAgentService = new CormAgentService(Collections.singletonList(new CormXmlListener()),
                CormBeanReloader.getInstance(), methodAdapterMap);
        return cormAgentService;
    }

    @Override
    public boolean isUseful() {
        try {
            Class.forName(COMTOP_SQL_SESSION_FACTORY);
        } catch (ClassNotFoundException e) {
            return false;
        }

        return true;
    }
}
