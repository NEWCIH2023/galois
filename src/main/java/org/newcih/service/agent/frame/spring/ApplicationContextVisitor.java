package org.newcih.service.agent.frame.spring;

import org.newcih.service.agent.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static org.newcih.constants.ClassNameConstant.ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT;
import static org.objectweb.asm.Opcodes.ASM9;
import static org.springframework.asm.Opcodes.*;


public class ApplicationContextVisitor extends MethodAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextVisitor.class);

    public ApplicationContextVisitor() {
        super(ANNOTATION_CONFIG_SERVLET_WEB_SERVER_APPLICATION_CONTEXT);
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

                mv.visitMethodInsn(INVOKESTATIC, "org/newcih/service/agent/frame/spring/SpringBeanReloader",
                        "getInstance", "()Lorg/newcih/service/agent/frame/spring/SpringBeanReloader;", false);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(CHECKCAST, "org/springframework/context/ApplicationContext");
                mv.visitMethodInsn(INVOKEVIRTUAL, "org/newcih/service/agent/frame/spring/SpringBeanReloader",
                        "setApplicationContext", "(Lorg/springframework/context/ApplicationContext;)V", false);

                if (logger.isDebugEnabled()) {
                    logger.debug("inject constructor by asm success !");
                }

            }

            super.visitInsn(opcode);
        }
    }
}
