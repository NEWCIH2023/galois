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

import io.liuguangsheng.galois.constants.FileType;
import io.liuguangsheng.galois.service.annotation.LazyBean;
import io.liuguangsheng.galois.service.monitor.FileChangedListener;
import io.liuguangsheng.galois.service.spring.SpringAgentService;
import io.liuguangsheng.galois.service.spring.SpringBeanReloader;
import io.liuguangsheng.galois.utils.ClassUtil;
import io.liuguangsheng.galois.utils.FileUtil;
import io.liuguangsheng.galois.utils.GaloisLog;
import org.slf4j.Logger;

import java.io.File;
import java.lang.instrument.ClassDefinition;


/**
 * Spring的Bean变动监听器
 *
 * @author liuguangsheng
 */
@LazyBean(value = "SpringBeanListener", manager = SpringAgentService.class)
public class SpringBeanListener implements FileChangedListener {

    private static final Logger logger = new GaloisLog(SpringBeanListener.class);
    private static final ClassChangedCache classChangedCache = ClassChangedCache.getInstance();
    private final SpringBeanReloader springBeanReloader = SpringBeanReloader.getInstance();

    @Override
    public boolean isSuitable(File file) {
        return FileUtil.matchFileType(file, FileType.CLASS_FILE);
    }

    /**
     * file changed handle
     *
     * @param classFile classFile
     */
    private void fileChangedHandle(File classFile) {

        try {
            // 结合class变动与java变动，当两者同时出现时，更新该class
            String className = ClassUtil.getClassNameFromClass(classFile);
            if (!classChangedCache.handleIfExisted(className)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("当前类{}处理失败，并未记录在缓存中", className);
                }
                return;
            } else {
                logger.debug("当前类{}处理成功，已将其移除缓存", className);
            }

            byte[] classBytes = FileUtil.readFile(classFile);
            Class<?> clazz = Class.forName(className);
            ClassDefinition definition = new ClassDefinition(clazz, classBytes);
            ClassUtil.getInstrumentation().redefineClasses(definition);

            if (springBeanReloader.isSuitable(clazz)) {
                springBeanReloader.updateBean(clazz);
            }

            logger.info("Redefine class file {} success.", classFile.getName());
        } catch (Throwable e) {
            logger.error("Reload Spring Bean fail.", e);
        }
    }

    @Override
    public void createdHandle(File file) {
        if (logger.isDebugEnabled()) {
            logger.debug("SpringBeanListener detect class file created: {}", file.getName());
        }

        fileChangedHandle(file);
    }

    /**
     * handler for file modifed
     *
     * @param file file
     */
    @Override
    public void modifiedHandle(File file) {
        if (logger.isDebugEnabled()) {
            logger.debug("SpringBeanListener detect class file modified: {}", file.getName());
        }

        fileChangedHandle(file);
    }

    @Override
    public String toString() {
        return SpringBeanListener.class.getSimpleName();
    }

    /**
     * handler for file deleted
     *
     * @param file file
     */
    @Override
    public void deletedHandle(File file) {
    }
}
