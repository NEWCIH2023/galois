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

package org.newcih.galois.service.spring.visitors;

import java.util.List;
import java.util.Objects;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.newcih.galois.service.MethodAdapter;
import org.newcih.galois.service.SpringRunnerManager;
import org.newcih.galois.service.annotation.AsmVisitor;
import org.newcih.galois.service.spring.SpringAgentService;
import org.springframework.boot.SpringApplicationRunListener;

import static jdk.internal.org.objectweb.asm.Opcodes.ALOAD;
import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;
import static jdk.internal.org.objectweb.asm.Opcodes.GETFIELD;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static jdk.internal.org.objectweb.asm.Opcodes.IRETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.POP;
import static jdk.internal.org.objectweb.asm.Opcodes.RETURN;
import static org.newcih.galois.constants.ClassNameConstant.SPRING_APPLICATION_RUN_LISTENERS;
import static org.newcih.galois.constants.Constant.DOT;
import static org.newcih.galois.constants.Constant.SLASH;

/**
 * spring application run listeners visitor
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
@AsmVisitor(value = "SpringApplicationRunListenersVisitor", manager = SpringAgentService.class)
public class SpringApplicationRunListenersVisitor extends MethodAdapter {

    /**
     * Instantiates a new Spring application run listeners visitor.
     */
    public SpringApplicationRunListenersVisitor() {
        super(SPRING_APPLICATION_RUN_LISTENERS);
    }

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        MethodVisitor mv = super.visitMethod(i, s, s1, s2, strings);

        if (Objects.equals(s, "<init>")) {
            return new RunMethod(ASM5, mv);
        }

        return mv;
    }

    public interface NecessaryMethods {

        List<SpringApplicationRunListener> getRunners();
    }

    private class RunMethod extends MethodVisitor {

        /**
         * Instantiates a new Run method.
         *
         * @param i             the
         * @param methodVisitor the method visitor
         */
        public RunMethod(int i, MethodVisitor methodVisitor) {
            super(i, methodVisitor);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode >= IRETURN && opcode <= RETURN) {
                String vClassName = SpringRunnerManager.class.getName().replace(DOT, SLASH);
                String pClassName = className.replace(DOT, SLASH);

                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, pClassName, "listeners", "Ljava/util/List;");
                mv.visitMethodInsn(INVOKESTATIC, vClassName, "getInstance", "()L" + vClassName + ";",
                        false);
                mv.visitMethodInsn(INVOKEVIRTUAL, vClassName, "getRunners", "()Ljava/util/List;", false);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z",
                        true);
                mv.visitInsn(POP);
                mv.visitInsn(RETURN);
                mv.visitEnd();
            }

            super.visitInsn(opcode);
        }
    }
}
