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

package org.newcih.galois.service.agent;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import org.newcih.galois.utils.GaloisLog;

import java.io.FileOutputStream;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;
import static org.newcih.galois.constants.FileType.CLASS_FILE;

public abstract class MethodAdapter extends ClassVisitor {

    private static final GaloisLog logger = GaloisLog.getLogger(MethodAdapter.class);
    protected final String className;
    protected ClassReader cr;
    protected ClassWriter cw;

    public MethodAdapter(String className) {
        super(ASM5);

        if (className == null || className.isEmpty()) {
            throw new NullPointerException("methodAdapter's classname cannot be null or empty");
        }

        this.className = className;
    }

    /**
     * convert byte[] of original class file
     *
     * @return
     */
    public byte[] transform() {

        try {
            cr = new ClassReader(className);
            cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
            cv = this.cw;
        } catch (Exception e) {
            logger.error("create new methodadapter for class {} failed!", className, e);
        }

        cr.accept(this, 0);
        byte[] result = cw.toByteArray();

        if (logger.isDebugEnabled()) {
            String tempClassFile = "" + getClass().getSimpleName() + CLASS_FILE.getFileType();
            try (FileOutputStream fos = new FileOutputStream(tempClassFile)) {
                fos.write(result);
            } catch (Throwable e) {
                logger.error("dump injected class file error", e);
            }
        }

        return result;
    }

    /**
     * check if methodadapter can injecte this version of service
     *
     * @return
     */
    public boolean isUseful() {
        return true;
    }
}
