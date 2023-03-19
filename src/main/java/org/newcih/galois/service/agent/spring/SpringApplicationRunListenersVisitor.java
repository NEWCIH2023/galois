package org.newcih.galois.service.agent.spring;

import java.util.Objects;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.newcih.galois.constants.ClassNameConstant;
import org.newcih.galois.service.agent.MethodAdapter;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static jdk.internal.org.objectweb.asm.Opcodes.IRETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.RETURN;
import static org.newcih.galois.constants.Constant.DOT;
import static org.newcih.galois.constants.Constant.SEMICOLON;
import static org.newcih.galois.constants.Constant.SLASH;

public class SpringApplicationRunListenersVisitor extends MethodAdapter {
    public SpringApplicationRunListenersVisitor() {
        super(ClassNameConstant.SPRING_APPLICATION_RUN_LISTENERS);
    }

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        MethodVisitor mv = super.visitMethod(i, s, s1, s2, strings);

        if (Objects.equals(s, "run") && Objects.equals(s1, "([Ljava/lang/String;)" +
                "Lorg/springframework/context/ConfigurableApplicationContext;")) {
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
                String className = "";
                mv.visitCode();
                mv.visitMethodInsn(INVOKESTATIC, className, "getInstance",
                        "()L" + (className.replace(DOT, SLASH)) + SEMICOLON, false);
                mv.visitMethodInsn(INVOKEVIRTUAL, className.replace(DOT, SLASH), "markStarted", "()V", false);
                mv.visitEnd();
            }

            super.visitInsn(opcode);
        }
    }
}
