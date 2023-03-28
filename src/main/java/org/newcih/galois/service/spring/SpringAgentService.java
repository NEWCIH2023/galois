/*
 * MIT License
 *
 * Copyright (c) [2023] [$user]
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

package org.newcih.galois.service.spring;

import static org.newcih.galois.constants.ConfConstant.RELOADER_SPRING_BOOT_ENABLE;
import org.newcih.galois.conf.GlobalConfiguration;
import org.newcih.galois.service.AgentService;
import org.newcih.galois.service.annotation.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * spring agent service
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
@Agent("SpringAgentService")
public class SpringAgentService extends AgentService {

  private static final Logger logger = LoggerFactory.getLogger(SpringAgentService.class);
  private static final GlobalConfiguration globalConfig = GlobalConfiguration.getInstance();
  private static final SpringAgentService instance = new SpringAgentService();

  /**
   * 当前AgentService是否可启用
   *
   * @return 当项目已经加载了必须的类之后，该AgentService将成为可用状态
   */
  @Override
  public boolean isUseful() {
    return super.isUseful() && globalConfig.getBoolean(RELOADER_SPRING_BOOT_ENABLE);
  }

  /**
   * Get instance spring agent service.
   *
   * @return the spring agent service
   */
  public static SpringAgentService getInstance() {
    return instance;
  }

}
