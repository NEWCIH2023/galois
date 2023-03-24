package org.newcih.galois.service.agent.corm;

import static jdk.internal.org.objectweb.asm.Opcodes.ALOAD;
import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;
import static jdk.internal.org.objectweb.asm.Opcodes.ATHROW;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static jdk.internal.org.objectweb.asm.Opcodes.IRETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.RETURN;
import static org.newcih.galois.constants.Constant.DOT;
import static org.newcih.galois.constants.Constant.SLASH;

import java.util.Objects;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.newcih.galois.constants.ClassNameConstant;
import org.newcih.galois.service.agent.MethodAdapter;

/**
 * comtop configuration visitor
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class ComtopConfigurationVisitor extends MethodAdapter {

  /**
   * Instantiates a new Comtop configuration visitor.
   */
  public ComtopConfigurationVisitor() {
    super(ClassNameConstant.COMTOP_CONFIGURATION);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
      String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);

    if (Objects.equals(name, "<init>") && Objects.equals(descriptor, "()V")) {
      return new ComtopConfigurationVisitor.ConstructorVisitor(ASM5, mv);
    }

    return mv;
  }

  /**
   * The type Constructor visitor.
   */
  class ConstructorVisitor extends MethodVisitor {

    /**
     * Instantiates a new Constructor visitor.
     *
     * @param api the api
     * @param mv  the mv
     */
    public ConstructorVisitor(int api, MethodVisitor mv) {
      super(api, mv);
    }

    @Override
    public void visitInsn(int opcode) {
      if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
        String pClassName = CormBeanReloader.class.getName().replace(DOT, SLASH);
        String vClassName = className.replace(DOT, SLASH);

        mv.visitCode();
        mv.visitMethodInsn(INVOKESTATIC, pClassName, "getInstance", "()L" + pClassName + ";",
            false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, pClassName, "setConfiguration", "(L" + vClassName + ";)V",
            false);
        mv.visitEnd();
      }

      super.visitInsn(opcode);
    }
  }
}
