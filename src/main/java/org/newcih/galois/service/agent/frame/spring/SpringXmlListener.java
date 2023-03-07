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

import org.newcih.galois.service.agent.FileChangedListener;

import java.io.File;

/**
 * Spring的XML配置文件变更监听处理
 */
public class SpringXmlListener implements FileChangedListener {

    /**
     * is listener useful for this file object
     *
     * @param file
     * @return
     */
    @Override
    public boolean isUseful(File file) {
        return false;
    }

    /**
     * handler for file created
     *
     * @param file
     */
    @Override
    public void createdHandle(File file) {

    }

    /**
     * handler for file modifed
     *
     * @param file
     */
    @Override
    public void modifiedHandle(File file) {

    }

    /**
     * handler for file deleted
     *
     * @param file
     */
    @Override
    public void deletedHandle(File file) {

    }
}
