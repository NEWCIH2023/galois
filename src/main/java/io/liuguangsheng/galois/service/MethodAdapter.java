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

package io.liuguangsheng.galois.service;

import static io.liuguangsheng.galois.constants.ConfConstant.PRINT_ASM_CODE_ENABLE;
import static io.liuguangsheng.galois.constants.Constant.DOT;
import static io.liuguangsheng.galois.constants.Constant.USER_DIR;
import static io.liuguangsheng.galois.constants.FileType.CLASS_FILE;
import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;
import io.liuguangsheng.galois.conf.GlobalConfiguration;
import io.liuguangsheng.galois.utils.GaloisLog;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Optional;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;

/**
 * method adapter
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public abstract class MethodAdapter extends ClassVisitor {

  private static final Logger logger = new GaloisLog(MethodAdapter.class);
  private static final GlobalConfiguration globalConfig = GlobalConfiguration.getInstance();
  private static int deleteActionCount = 0;
  /**
   * The Class name.
   */
  protected final String className;
  /**
   * The Cr.
   */
  protected ClassReader cr;
  /**
   * The Cw.
   */
  protected ClassWriter cw;

  /**
   * Instantiates a new Method adapter.
   *
   * @param className the class name
   */
  protected MethodAdapter(String className) {
    super(ASM5);

    if (className == null || className.isEmpty()) {
      throw new NullPointerException("MethodAdapter's class name cannot be null or empty.");
    }

    this.className = className;
  }

  /**
   * Add method.
   */
  protected void beforeTransform() {
  }

  /**
   * convert byte[] of original class file
   *
   * @return the byte []
   */
  public byte[] transform() {

    try {
      cr = new ClassReader(className);
      // COMPUTE_MAXS means automatically compute the maximum stack size and the maximum number of local variables
      // of methods.
      // COMPUTE_FRAMES means automatically compute the stack map frames of methods from scratch.
      cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
      cv = this.cw;
    } catch (Exception e) {
      logger.error("Create new methodAdapter for class {} fail.", className, e);
    }

    cr.accept(this, 0);
    beforeTransform();
    byte[] result = cw.toByteArray();
    debugClassFile(result);

    return result;
  }

  private void debugClassFile(byte[] result) {
    // 清空之前生成的.class文件
    if (++deleteActionCount == 1) {
      Optional.ofNullable(
          new File(globalConfig.getString(USER_DIR))
              .listFiles(file -> file.getName().endsWith(CLASS_FILE.getFileType()))
      ).ifPresent(files ->
          Arrays.stream(files).forEach(File::delete)
      );
    }

    // 生成.class
    if (globalConfig.getBoolean(PRINT_ASM_CODE_ENABLE, false)) {
      String newClassName = className.substring(className.lastIndexOf(DOT) + 1);
      String tempClassFile = newClassName + CLASS_FILE.getFileType();

      try (FileOutputStream fos = new FileOutputStream(tempClassFile)) {
        fos.write(result);
      } catch (Throwable e) {
        logger.error("Dump class file error.", e);
      }
      logger.info("Had dump class file to {}.", tempClassFile);
    }

  }

  /**
   * check if methodadapter can injecte this version of service
   *
   * @return the boolean
   */
  public boolean isUseful() {
    return true;
  }

  /**
   * Gets class name.
   *
   * @return the class name
   */
  public String getClassName() {
    return className;
  }
}
