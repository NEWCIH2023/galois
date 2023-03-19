package org.newcih.galois.service.agent.spring;

import java.util.Objects;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.newcih.galois.constants.ClassNameConstant;
import org.newcih.galois.service.agent.MethodAdapter;

import static jdk.internal.org.objectweb.asm.Opcodes.ALOAD;
import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;
import static jdk.internal.org.objectweb.asm.Opcodes.ASTORE;
import static jdk.internal.org.objectweb.asm.Opcodes.GETFIELD;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static jdk.internal.org.objectweb.asm.Opcodes.IRETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.POP;
import static jdk.internal.org.objectweb.asm.Opcodes.RETURN;
import static org.newcih.galois.constants.Constant.DOT;
import static org.newcih.galois.constants.Constant.SLASH;

public class SpringApplicationRunListenersVisitor extends MethodAdapter {
    public SpringApplicationRunListenersVisitor() {
        super(ClassNameConstant.SPRING_APPLICATION_RUN_LISTENERS);
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
                mv.visitCode();
                mv.visitMethodInsn(INVOKEVIRTUAL, className.replace(DOT, SLASH), "getRunners", "()Ljava/util/List;",
                        false);
                mv.visitVarInsn(ASTORE, 1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className.replace(DOT, SLASH), "listeners", "Ljava/util/List;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true);
                mv.visitInsn(POP);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }

            super.visitInsn(opcode);
        }
    }
}
