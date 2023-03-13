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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AgentService {
    /**
     * 文件变更监听器列表
     */
    protected List<FileChangedListener> listeners = new ArrayList<>(4);
    /**
     * 对应的Bean重载的service
     */
    protected BeanReloader<?> beanReloader;
    /**
     * 类名到MethodAdapter的映射
     */
    protected Map<String, MethodAdapter> adapterMap = new HashMap<>(4);
    /**
     * 是否启用该AgentService，当该变量值与necessaryClasses的大小一致时，表示该AgentService启用
     */
    private int enabled;
    protected static List<String> necessaryClasses = new ArrayList<>(8);

    public boolean isUseful() {
        return enabled == necessaryClasses.size();
    }

    /**
     * 通过getInstance获取对象时，不应该执行初始化步骤，有些特殊依赖的类此时并不会存在，因为这个AgentService
     * 不一定会被启用，所以项目中也不一定存在对应的import的类。所以要等到isUseful为true的时候，才来执行init方法，
     * 完成AgentService的初始化
     */
    public abstract void init();

    public boolean checkAgentEnable(String loadedClassName) {
        if (necessaryClasses.contains(loadedClassName)) {
            enabled++;
            return true;
        }

        return false;
    }

    public List<FileChangedListener> getListeners() {
        return listeners;
    }

    public BeanReloader<?> getBeanReloader() {
        return beanReloader;
    }

    public Map<String, MethodAdapter> getAdapterMap() {
        return adapterMap;
    }
}
