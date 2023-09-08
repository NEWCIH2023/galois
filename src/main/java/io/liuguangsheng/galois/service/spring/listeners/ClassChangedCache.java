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

package io.liuguangsheng.galois.service.spring.listeners;

import io.liuguangsheng.galois.utils.GaloisLog;
import org.slf4j.Logger;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * record the changed java file name
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class ClassChangedCache {

    private static final Logger logger = new GaloisLog(ClassChangedCache.class);
    private final Set<String> changedClassNames = new CopyOnWriteArraySet<>();

    private static class ClassChangedCacheHolder {
        private static final ClassChangedCache instance = new ClassChangedCache();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ClassChangedCache getInstance() {
        return ClassChangedCacheHolder.instance;
    }

    /**
     * Add changed class name.
     *
     * @param className the class name
     */
    public void hadChanged(String className) {
        if (logger.isDebugEnabled()) {
            logger.debug("记录到类{}源码发生变动.", className);
        }

        changedClassNames.add(className);
    }

    /**
     * Class done.
     *
     * @param className the class name
     */
    public boolean handleIfExisted(String className) {
        if (logger.isDebugEnabled()) {
            logger.debug("类{}的源码变动已被处理.", className);
        }

        return changedClassNames.remove(className);
    }

    /**
     * Clear class name.
     */
    public void clearCache() {
        if (logger.isDebugEnabled()) {
            logger.debug("已清空类源码变动缓存.");
        }

        changedClassNames.clear();
    }

    public void printCache() {
        if (logger.isDebugEnabled()) {
            logger.debug("当前类源码变动缓存如下");
        }

        changedClassNames.forEach(name -> System.out.print(name + ",\t"));
    }
}
