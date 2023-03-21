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

package org.newcih.galois.service.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.newcih.galois.conf.GlobalConfiguration;
import org.newcih.galois.service.FileChangedListener;

/**
 * abstract agent service
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public abstract class AgentService {

  protected static final GlobalConfiguration globalConfig = GlobalConfiguration.getInstance();
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
   * 配置文件对应的AgentService名称
   */
  protected String confAgentName;
  /**
   * 必备的加载类名称列表
   */
  protected List<String> necessaryClasses = new ArrayList<>(8);
  /**
   * 是否启用该AgentService，当该变量值与necessaryClasses的大小一致时，表示该AgentService启用
   */
  private int enabled;
  /**
   * 是否初始化完成
   */
  private boolean inited;

  /**
   * 当前AgentService是否可启用
   *
   * @return 当项目已经加载了必须的类之后，该AgentService将成为可用状态
   */
  public boolean isUseful() {
    return enabled == necessaryClasses.size() && globalConfig.getBoolean(confAgentName, true);
  }

  /**
   * 通过getInstance获取对象时，不应该执行初始化步骤，有些特殊依赖的类此时并不会存在，因为这个AgentService
   * 不一定会被启用，所以项目中也不一定存在对应的import的类。所以要等到isUseful为true的时候，才来执行init方法， 完成AgentService的初始化
   */
  public void init() {
    inited = true;
  }

  /**
   * 检测到当前已经加载了名为loadedClassName的类时，则更新该AgentService的enabled值，使其++，当enabled值等于
   * necessaryClasses的大小时，表示该agentService正式启用
   *
   * @param loadedClassName loaded class name
   * @return 项目是否加载了对应的类名的类
   */
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

  public boolean isInited() {
    return inited;
  }

  public void setInited(boolean inited) {
    this.inited = inited;
  }

  public void setEnabled(int enabled) {
    this.enabled = enabled;
  }

  public int getEnabled() {
    return enabled;
  }

  public String getConfAgentName() {
    return confAgentName;
  }

  public List<String> getNecessaryClasses() {
    return necessaryClasses;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
