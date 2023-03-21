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

package org.newcih.galois.service.agent.spring;

import static org.newcih.galois.constants.FileType.CLASS_FILE;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import org.newcih.galois.service.FileChangedListener;
import org.newcih.galois.utils.FileUtil;
import org.newcih.galois.utils.GaloisLog;
import org.newcih.galois.utils.JavaUtil;


/**
 * Spring的Bean变动监听器
 */
public class SpringBeanListener implements FileChangedListener {

  public static final GaloisLog logger = GaloisLog.getLogger(SpringBeanListener.class);
  private final static SpringBeanReloader reloader = SpringBeanReloader.getInstance();

  @Override
  public boolean isUseful(File file) {
    return FileUtil.validFileType(file, CLASS_FILE);
  }

  private void fileChangedHandle(File classFile) {
    String className = JavaUtil.getClassNameFromClass(classFile);
    byte[] classBytes = FileUtil.readFile(classFile);

    try {
      Class<?> clazz = Class.forName(className);
      ClassDefinition definition = new ClassDefinition(clazz, classBytes);
      JavaUtil.getInst().redefineClasses(definition);
      logger.info("had redefine class file => {}", classFile.getName());

//            if (reloader.isUseful(clazz)) {
//                reloader.updateBean(clazz);
//            }
    } catch (Throwable e) {
      logger.error("reload bean failed", e);
    }
  }

  @Override
  public void createdHandle(File file) {
    if (logger.isDebugEnabled()) {
      logger.debug("spring bean listener monitor java file created event ==> {}", file.getName());
    }

    fileChangedHandle(file);
  }

  /**
   * handler for file modifed
   *
   * @param file
   */
  @Override
  public void modifiedHandle(File file) {
    if (logger.isDebugEnabled()) {
      logger.debug("spring bean listener monitor java file modified event ==> {}", file.getName());
    }

    fileChangedHandle(file);
  }

  @Override
  public String toString() {
    return "SpringBeanListener";
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
