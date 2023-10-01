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

package io.liuguangsheng.galois.service.spring;

import io.liuguangsheng.galois.conf.GlobalConfiguration;
import io.liuguangsheng.galois.constants.ConfConstant;
import io.liuguangsheng.galois.service.AgentService;

/**
 * spring agent service
 *
 * @author liuguangsheng
 */
public class SpringAgentService extends AgentService {

    private static final GlobalConfiguration config = GlobalConfiguration.getInstance();

    private static class SpringAgentServiceHolder {
        private static final SpringAgentService instance = new SpringAgentService();
    }

    /**
     * Get instance spring agent service.
     *
     * @return the spring agent service
     */
    public static SpringAgentService getInstance() {
        return SpringAgentServiceHolder.instance;
    }

    /**
     * 当前AgentService是否可启用
     *
     * @return 当项目已经加载了必须的类之后，该AgentService将成为可用状态
     */
    @Override
    public boolean isSuitable() {
        return super.isSuitable() && config.getBool(ConfConstant.RELOADER_SPRING_BOOT_ENABLE);
    }

}
