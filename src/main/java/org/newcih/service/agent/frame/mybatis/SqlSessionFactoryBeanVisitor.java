package org.newcih.service.agent.frame.mybatis;

import org.newcih.service.agent.MethodAdapter;
import org.newcih.utils.GaloisLog;
import org.objectweb.asm.MethodVisitor;

import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

public class SqlSessionFactoryBeanVisitor extends MethodAdapter {

    private static final GaloisLog logger = GaloisLog.getLogger(SqlSessionFactoryBeanVisitor.class);

    public SqlSessionFactoryBeanVisitor() {
        super(MyBatisTransformer.SQL_SESSION_FACTORY_BEAN);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                     String[] exceptions) {

        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);

        if (Objects.equals("<init>", name)) {
            return new ConstructorVisitor(ASM9, mv);
        }

        return mv;
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

                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, "org/newcih/service/agent/frame/mybatis/MyBatisBeanReloader",
                        "getInstance", "()Lorg/newcih/service/agent/frame/mybatis/MyBatisBeanReloader;", false);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "org/newcih/service/agent/frame/mybatis/MyBatisBeanReloader",
                        "setConfiguration", "(Lorg/apache/ibatis/session/Configuration;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();

                if (logger.isDebugEnabled()) {
                    logger.debug("inject constructor by asm success");
                }

            }
        }
    }

}
