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

import java.util.List;
import java.util.Map;

public abstract class AgentService {

    private final List<FileChangedListener> listener;
    private final BeanReloader<?> beanReloader;
    private final Map<String, MethodAdapter> classNameToMethodMap;
    private boolean enabled;

    public AgentService(List<FileChangedListener> listener, BeanReloader<?> beanReloader,
                        Map<String, MethodAdapter> classNameToMethodMap) {
        this.listener = listener;
        this.beanReloader = beanReloader;
        this.classNameToMethodMap = classNameToMethodMap;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<FileChangedListener> getListener() {
        return listener;
    }

    public BeanReloader<?> getBeanReloader() {
        return beanReloader;
    }

    public Map<String, MethodAdapter> getClassNameToMethodMap() {
        return classNameToMethodMap;
    }
}
