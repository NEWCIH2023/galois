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

package org.newcih.galois.service.watch.frame.spring;

import org.newcih.galois.service.agent.frame.spring.SpringBeanReloader;
import org.newcih.galois.service.watch.ProjectFileManager;
import org.newcih.galois.service.watch.frame.FileChangedListener;
import org.newcih.galois.utils.FileUtils;
import org.newcih.galois.utils.GaloisLog;
import org.newcih.galois.utils.SystemUtil;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.Objects;

import static org.newcih.galois.constants.FileTypeConstant.CLASS_FILE;

/**
 * Spring的Bean变动监听器
 */
public final class SpringBeanListener implements FileChangedListener {

    public static final GaloisLog logger = GaloisLog.getLogger(SpringBeanListener.class);
    private final static SpringBeanReloader reloader = SpringBeanReloader.getInstance();
    private final static ProjectFileManager fileManager = ProjectFileManager.getInstance();
    private final Instrumentation inst;

    public SpringBeanListener(Instrumentation inst) {
        this.inst = inst;
    }

    @Override
    public boolean validFile(File file) {
        return Objects.equals(FileUtils.getFileType(file), CLASS_FILE);
    }


    @Override
    public void fileCreatedHandle(File file) {
        String className = SystemUtil.getClassName(fileManager.getClassPath(), file);

        try {
            Class<?>[] classes = inst.getAllLoadedClasses();
            for (Class<?> clazz : classes) {
                if (clazz.getName().equals(className)) {
                    ClassDefinition newClassDef = new ClassDefinition(clazz, SystemUtil.readFile(file));
                    inst.redefineClasses(newClassDef);
                    Object newBean = clazz.newInstance();

                    if (reloader.validBean(newBean)) {
                        reloader.updateBean(newBean);
                    }
                    break;
                }
            }
        } catch (Throwable e) {
            logger.error("reload bean under file create event failed", e);
        }
    }

    @Override
    public void fileModifiedHandle(File file) {
        // 不可以在变动时处理，此时class文件经历删除-新增，该阶段可能找不到class文件
    }

    @Override
    public void fileDeletedHandle(File file) {

    }
}
