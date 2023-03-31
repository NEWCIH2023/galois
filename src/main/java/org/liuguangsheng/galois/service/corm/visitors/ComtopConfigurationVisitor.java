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

package org.liuguangsheng.galois.service.corm.visitors;

import com.comtop.corm.session.Configuration;
import java.util.Objects;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.liuguangsheng.galois.service.MethodAdapter;
import org.liuguangsheng.galois.service.annotation.AsmVisitor;
import org.liuguangsheng.galois.service.corm.CormAgentService;
import org.liuguangsheng.galois.service.corm.CormBeanReloader;

import static jdk.internal.org.objectweb.asm.Opcodes.ALOAD;
import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;
import static jdk.internal.org.objectweb.asm.Opcodes.ATHROW;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static jdk.internal.org.objectweb.asm.Opcodes.IRETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.RETURN;
import static org.liuguangsheng.galois.constants.ClassNameConstant.CT_CONFIGURATION;
import static org.liuguangsheng.galois.constants.Constant.DOT;
import static org.liuguangsheng.galois.constants.Constant.SLASH;

/**
 * comtop configuration visitor
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
@AsmVisitor(value = "ComtopConfigurationVisitor", manager = CormAgentService.class)
public class ComtopConfigurationVisitor extends MethodAdapter {

    /**
     * Instantiates a new Comtop configuration visitor.
     */
    public ComtopConfigurationVisitor() {
        super(CT_CONFIGURATION);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                     String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);

        if (Objects.equals(name, "<init>") && Objects.equals(descriptor, "()V")) {
            return new ComtopConfigurationVisitor.ConstructorVisitor(ASM5, mv);
        }

        return mv;
    }

    public interface NecessaryMethods {

        void setConfiguration(Configuration configuration);
    }

    /**
     * The type Constructor visitor.
     */
    class ConstructorVisitor extends MethodVisitor {

        /**
         * Instantiates a new Constructor visitor.
         *
         * @param api the api
         * @param mv  the mv
         */
        public ConstructorVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                String pClassName = CormBeanReloader.class.getName().replace(DOT, SLASH);
                String vClassName = className.replace(DOT, SLASH);

                mv.visitCode();
                mv.visitMethodInsn(INVOKESTATIC, pClassName, "getInstance", "()L" + pClassName + ";", false);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, pClassName, "setConfiguration", "(L" + vClassName + ";)V", false);
                mv.visitEnd();
            }

            super.visitInsn(opcode);
        }
    }
}
