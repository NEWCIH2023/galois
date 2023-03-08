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

import org.newcih.galois.service.ProjectFileManager;
import org.newcih.galois.service.agent.FileChangedListener;
import org.newcih.galois.utils.FileUtil;
import org.newcih.galois.utils.GaloisLog;
import org.newcih.galois.utils.JavaUtil;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.util.Objects;

import static org.newcih.galois.constants.FileTypeConstant.JAVA_FILE;

/**
 * Spring的Bean变动监听器
 */
public final class SpringBeanListener implements FileChangedListener {

    public static final GaloisLog logger = GaloisLog.getLogger(SpringBeanListener.class);
    private final static SpringBeanReloader reloader = SpringBeanReloader.getInstance();
    private final static ProjectFileManager fileManager = ProjectFileManager.getInstance();

    @Override
    public boolean isUseful(File file) {
        return Objects.equals(FileUtil.getFileType(file), JAVA_FILE);
    }

    private void fileChangedHandle(File file) {
        String className = "";

        try {
            Class<?>[] classes = JavaUtil.getInst().getAllLoadedClasses();
            for (Class<?> clazz : classes) {

                if (clazz.getName().equals(className)) {
                    ClassDefinition newClassDef = new ClassDefinition(clazz, FileUtil.readFile(file));
                    JavaUtil.getInst().redefineClasses(newClassDef);
                    Object newBean = clazz.newInstance();
                    // there should update bean in spring context if spring managed this bean
                    if (reloader.isUseful(newBean)) {
                        reloader.updateBean(newBean);
                    }
                    break;
                }

            }
        } catch (Throwable e) {
            logger.error("reload bean under file created event failed", e);
        }
    }

    @Override
    public void createdHandle(File file) {
        fileChangedHandle(file);
    }

    /**
     * handler for file modifed
     *
     * @param file
     */
    @Override
    public void modifiedHandle(File file) {
        fileChangedHandle(file);
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
