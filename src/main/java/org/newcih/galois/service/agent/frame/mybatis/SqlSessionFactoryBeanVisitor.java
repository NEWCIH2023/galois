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

package org.newcih.galois.service.agent.frame.mybatis;

import org.newcih.galois.service.agent.MethodAdapter;
import org.newcih.galois.utils.GaloisLog;
import org.objectweb.asm.MethodVisitor;

import java.util.Objects;

import static org.newcih.galois.constants.ClassNameConstant.DEFAULT_SQL_SESSION_FACTORY;
import static org.objectweb.asm.Opcodes.*;

public class SqlSessionFactoryBeanVisitor extends MethodAdapter {

    private static final GaloisLog logger = GaloisLog.getLogger(SqlSessionFactoryBeanVisitor.class);

    public SqlSessionFactoryBeanVisitor() {
        super(DEFAULT_SQL_SESSION_FACTORY);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                     String[] exceptions) {

        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);

        if (Objects.equals("<init>", name)) {
            return new ConstructorVisitor(ASM5, mv);
        }

        return mv;
    }

    @Override
    public boolean usable() {
        return true;
    }

    /**
     * 构造函数修改
     */
    static class ConstructorVisitor extends MethodVisitor {

        public ConstructorVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                String reloaderClassName = MyBatisBeanReloader.class.getName().replace(".", "/");

                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, reloaderClassName, "getInstance", "()L" + reloaderClassName + ";",
                        false);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, reloaderClassName,
                        "setConfiguration", "(Lorg/apache/ibatis/session/Configuration;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();

                if (logger.isDebugEnabled()) {
                    logger.debug("injected sqlSessionFactoryBean constructor by ASM success!");
                }

            }
        }
    }

}
