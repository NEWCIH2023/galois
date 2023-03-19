package org.newcih.galois.service.agent.spring;

import java.util.Objects;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.newcih.galois.constants.ClassNameConstant;
import org.newcih.galois.service.agent.MethodAdapter;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;
import static jdk.internal.org.objectweb.asm.Opcodes.ATHROW;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static jdk.internal.org.objectweb.asm.Opcodes.IRETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.RETURN;

public class SpringApplicationVisitor extends MethodAdapter {
    public SpringApplicationVisitor() {
        super(ClassNameConstant.SPRING_APPLICATION);
    }

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        MethodVisitor mv = super.visitMethod(i, s, s1, s2, strings);

        if (Objects.equals(s, "run")) {
            return new SpringApplicationVisitor.RunMethod(ASM5, mv);
        }

        return mv;
    }

    static class RunMethod extends MethodVisitor {

        public RunMethod(int i, MethodVisitor methodVisitor) {
            super(i, methodVisitor);
        }

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                mv.visitCode();
                mv.visitMethodInsn(INVOKESTATIC, "org/newcih/galois/service/SpringBootLifeCycle", "getInstance", "()" +
                        "Lorg/newcih/galois/service/SpringBootLifeCycle;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "org/newcih/galois/service/SpringBootLifeCycle", "markStarted", "()" +
                        "V", false);
                mv.visitInsn(RETURN);
                mv.visitEnd();
            }
        }
    }
}
