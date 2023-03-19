package org.newcih.galois.service.agent.corm;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.newcih.galois.constants.ClassNameConstant;
import org.newcih.galois.service.agent.MethodAdapter;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;

public class ComtopConfigurationVisitor extends MethodAdapter {
    public ComtopConfigurationVisitor() {
        super(ClassNameConstant.COMTOP_CONFIGURATION);
    }

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        return super.visitMethod(i, s, s1, s2, strings);
    }

    static class ConstructorVisitor extends MethodVisitor {
        public ConstructorVisitor() {
            super(ASM5);
        }

        @Override
        public void visitMethodInsn(int i, String s, String s1, String s2, boolean b) {
            super.visitMethodInsn(i, s, s1, s2, b);
        }
    }
}
