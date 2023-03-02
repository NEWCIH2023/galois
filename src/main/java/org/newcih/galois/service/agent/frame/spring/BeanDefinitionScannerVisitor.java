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

import org.newcih.galois.service.agent.MethodAdapter;
import org.newcih.galois.utils.GaloisLog;
import org.objectweb.asm.MethodVisitor;

import java.util.Objects;

import static org.newcih.galois.constants.ClassNameConstant.CLASS_PATH_BEAN_DEFINITION_SCANNER;
import static org.objectweb.asm.Opcodes.*;

public class BeanDefinitionScannerVisitor extends MethodAdapter {

    private static final GaloisLog logger = GaloisLog.getLogger(BeanDefinitionScannerVisitor.class);

    public BeanDefinitionScannerVisitor() {
        super(CLASS_PATH_BEAN_DEFINITION_SCANNER);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                     String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        if (Objects.equals("doScan", name)) {
            return new DoScanMethodVisitor(ASM9, mv);
        }

        return mv;
    }

    static class DoScanMethodVisitor extends MethodVisitor {

        public DoScanMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                String reloaderClassName = SpringBeanReloader.class.getName().replace(".", "/");

                mv.visitCode();
                mv.visitMethodInsn(INVOKESTATIC, reloaderClassName, "getInstance", "()L" + reloaderClassName + ";",
                        false);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, reloaderClassName, "setScanner"
                        , "(Lorg/springframework/context/annotation/ClassPathBeanDefinitionScanner;)V", false);

                if (logger.isDebugEnabled()) {
                    logger.debug("injected doScan method in springBeanReloader by ASM success!");
                }
            }

            super.visitInsn(opcode);
        }
    }

}
