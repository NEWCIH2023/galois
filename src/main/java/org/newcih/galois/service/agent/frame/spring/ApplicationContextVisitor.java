package org.newcih.galois.service.agent.frame.spring;

import org.newcih.galois.constants.ClassNameConstant;
import org.newcih.galois.service.agent.MethodAdapter;
import org.newcih.galois.utils.GaloisLog;
import org.objectweb.asm.MethodVisitor;

import java.util.Objects;

import static org.objectweb.asm.Opcodes.ASM9;
import static org.springframework.asm.Opcodes.*;


public class ApplicationContextVisitor extends MethodAdapter {

    private static final GaloisLog logger = GaloisLog.getLogger(ApplicationContextVisitor.class);

    public ApplicationContextVisitor() {
        super(ClassNameConstant.ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                     String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        if (Objects.equals("<init>", name) && Objects.equals(descriptor, "()V")) {
            return new ConstructorVisiter(ASM9, mv);
        }

        return mv;
    }

    static class ConstructorVisiter extends MethodVisitor {
        public ConstructorVisiter(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                String reloaderClassName = SpringBeanReloader.class.getName().replace(".", "/");

                mv.visitMethodInsn(INVOKESTATIC, reloaderClassName, "getInstance", "()L" + reloaderClassName + ";",
                        false);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(CHECKCAST, "org/springframework/context/ApplicationContext");
                mv.visitMethodInsn(INVOKEVIRTUAL, reloaderClassName,
                        "setApplicationContext", "(Lorg/springframework/context/ApplicationContext;)V", false);

                if (logger.isDebugEnabled()) {
                    logger.debug("injected applicationContext constructor by ASM success!");
                }
            }

            super.visitInsn(opcode);
        }
    }
}
