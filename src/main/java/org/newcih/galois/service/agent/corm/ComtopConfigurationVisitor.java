package org.newcih.galois.service.agent.corm;

import java.util.Objects;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.newcih.galois.constants.ClassNameConstant;
import org.newcih.galois.service.agent.MethodAdapter;
import org.newcih.galois.utils.GaloisLog;

import static jdk.internal.org.objectweb.asm.Opcodes.ALOAD;
import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;
import static jdk.internal.org.objectweb.asm.Opcodes.ATHROW;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static jdk.internal.org.objectweb.asm.Opcodes.IRETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.RETURN;
import static org.newcih.galois.constants.Constant.DOT;
import static org.newcih.galois.constants.Constant.SEMICOLON;
import static org.newcih.galois.constants.Constant.SLASH;

public class ComtopConfigurationVisitor extends MethodAdapter {
    private static final GaloisLog logger = GaloisLog.getLogger(ComtopConfigurationVisitor.class);

    public ComtopConfigurationVisitor() {
        super(ClassNameConstant.COMTOP_CONFIGURATION);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                     String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);

        if (Objects.equals(name, "<init>")) {
            return new ComtopConfigurationVisitor.ConstructorVisitor(ASM5, mv);
        }

        return mv;
    }

    static class ConstructorVisitor extends MethodVisitor {
        public ConstructorVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                String className = CormBeanReloader.class.getName().replace(DOT, SLASH);

                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, className, "getInstance", "()L" + className + SEMICOLON, false);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, className, "setConfiguration", "(Lcom/comtop/corm/session" +
                        "/Configuration;)V", false);
                mv.visitInsn(RETURN);
                mv.visitEnd();

                if (logger.isDebugEnabled()) {
                    logger.debug("injected corm configuration constructor success!");
                }
            }
        }
    }
}
