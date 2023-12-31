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

package io.liuguangsheng.galois.service.mybatis;

import io.liuguangsheng.galois.conf.GlobalConfiguration;
import io.liuguangsheng.galois.service.AgentService;

import static io.liuguangsheng.galois.constants.ConfConstant.RELOADER_MYBATIS_ENABLE;

/**
 * mybatis agent service
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class MyBatisAgentService extends AgentService {

    private static final GlobalConfiguration config = GlobalConfiguration.getInstance();

    private static class MyBatisAgentServiceHolder {
        private static final MyBatisAgentService instance = new MyBatisAgentService();
    }

    private MyBatisAgentService() {
    }

    /**
     * get instance
     *
     * @return {@link MyBatisAgentService}
     * @see MyBatisAgentService
     */
    public static MyBatisAgentService getInstance() {
        return MyBatisAgentServiceHolder.instance;
    }

    @Override
    public boolean isSuitable() {
        return super.isSuitable() && config.getBool(RELOADER_MYBATIS_ENABLE);
    }


}
