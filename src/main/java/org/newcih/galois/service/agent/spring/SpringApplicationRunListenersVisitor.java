package org.newcih.galois.service.agent.spring;

import java.util.Objects;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.newcih.galois.service.agent.MethodAdapter;

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

public class SpringApplicationRunListenersVisitor extends MethodAdapter {
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

    static class RunMethod extends MethodVisitor {

        public RunMethod(int i, MethodVisitor methodVisitor) {
            super(i, methodVisitor);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode >= IRETURN && opcode <= RETURN) {
                String className = SpringAgentService.class.getName();
                String slashClassName = className.replace(DOT, SLASH);
                String slashRunnerClassName = SPRING_APPLICATION_RUN_LISTENERS.replace(DOT, SLASH);

                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, slashRunnerClassName, "listeners", "Ljava/util/List;");
                mv.visitMethodInsn(INVOKESTATIC, slashClassName, "getInstance", "()L" + slashClassName + ";", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, slashClassName, "getRunners", "()Ljava/util/List;", false);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true);
                mv.visitInsn(POP);
                mv.visitInsn(RETURN);
                mv.visitEnd();
            }

            super.visitInsn(opcode);
        }
    }
}