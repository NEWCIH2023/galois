package io.liuguangsheng.galois.service.spring.visitors;

import static jdk.internal.org.objectweb.asm.Opcodes.ACC_FINAL;
import static jdk.internal.org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static jdk.internal.org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static jdk.internal.org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static jdk.internal.org.objectweb.asm.Opcodes.ALOAD;
import static jdk.internal.org.objectweb.asm.Opcodes.ARETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.ASTORE;
import static jdk.internal.org.objectweb.asm.Opcodes.ATHROW;
import static jdk.internal.org.objectweb.asm.Opcodes.CHECKCAST;
import static jdk.internal.org.objectweb.asm.Opcodes.DUP;
import static jdk.internal.org.objectweb.asm.Opcodes.GETFIELD;
import static jdk.internal.org.objectweb.asm.Opcodes.GOTO;
import static jdk.internal.org.objectweb.asm.Opcodes.IFEQ;
import static jdk.internal.org.objectweb.asm.Opcodes.IFNULL;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static jdk.internal.org.objectweb.asm.Opcodes.RETURN;
import static jdk.internal.org.objectweb.asm.TypeReference.INSTANCEOF;
import static jdk.internal.org.objectweb.asm.TypeReference.NEW;
import io.liuguangsheng.galois.constants.ClassNameConstant;
import io.liuguangsheng.galois.service.MethodAdapter;
import io.liuguangsheng.galois.service.annotation.AsmVisitor;
import io.liuguangsheng.galois.service.spring.SpringAgentService;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;

/**
 * The type Handler method mapping visitor.
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
@AsmVisitor(value = "HandlerMethodMappingVisitor", manager = SpringAgentService.class)
public class HandlerMethodMappingVisitor extends MethodAdapter {

  /**
   * Instantiates a new Method adapter.
   */
  public HandlerMethodMappingVisitor() {
    super(ClassNameConstant.ABSTRACT_HANDLER_METHOD_MAPPING);
  }

  /**
   * Add method.
   */
  @Override
  public void beforeTransform() {
    {
      MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "updateHandlerMethods", "(Ljava/lang/Object;)V", null, null);
      mv.visitParameter("handler", 0);
      mv.visitCode();
      mv.visitVarInsn(ALOAD, 1);
      mv.visitTypeInsn(INSTANCEOF, "java/lang/String");
      Label l0 = new Label();
      mv.visitJumpInsn(IFEQ, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKEVIRTUAL, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "obtainApplicationContext",
          "()Lorg/springframework/context/ApplicationContext;", false);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitTypeInsn(CHECKCAST, "java/lang/String");
      mv.visitMethodInsn(INVOKEINTERFACE, "org/springframework/context/ApplicationContext", "getType", "(Ljava/lang/String;)Ljava/lang/Class;", true);
      Label l1 = new Label();
      mv.visitJumpInsn(GOTO, l1);
      mv.visitLabel(l0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
      mv.visitLabel(l1);
      mv.visitVarInsn(ASTORE, 2);
      mv.visitVarInsn(ALOAD, 2);
      Label l2 = new Label();
      mv.visitJumpInsn(IFNULL, l2);
      mv.visitVarInsn(ALOAD, 2);
      mv.visitMethodInsn(INVOKESTATIC, "org/springframework/util/ClassUtils", "getUserClass", "(Ljava/lang/Class;)Ljava/lang/Class;", false);
      mv.visitVarInsn(ASTORE, 3);
      mv.visitVarInsn(ALOAD, 3);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 3);
      mv.visitInvokeDynamicInsn("inspect",
          "(Lorg/springframework/web/servlet/handler/AbstractHandlerMethodMapping;Ljava/lang/Class;)Lorg/springframework/core/MethodIntrospector$MetadataLookup;",
          new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
              "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;"
                  + "Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"),
          new Object[]{Type.getType("(Ljava/lang/reflect/Method;)Ljava/lang/Object;"),
              new Handle(Opcodes.H_INVOKESPECIAL, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "lambda$updateHandlerMethods$1",
                  "(Ljava/lang/Class;Ljava/lang/reflect/Method;)Ljava/lang/Object;"), Type.getType("(Ljava/lang/reflect/Method;)Ljava/lang/Object;")});
      mv.visitMethodInsn(INVOKESTATIC, "org/springframework/core/MethodIntrospector", "selectMethods",
          "(Ljava/lang/Class;Lorg/springframework/core/MethodIntrospector$MetadataLookup;)Ljava/util/Map;", false);
      mv.visitVarInsn(ASTORE, 4);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "logger", "Lorg/apache/commons/logging/Log;");
      mv.visitMethodInsn(INVOKEINTERFACE, "org/apache/commons/logging/Log", "isTraceEnabled", "()Z", true);
      Label l3 = new Label();
      mv.visitJumpInsn(IFEQ, l3);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "logger", "Lorg/apache/commons/logging/Log;");
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 3);
      mv.visitVarInsn(ALOAD, 4);
      mv.visitMethodInsn(INVOKESPECIAL, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "formatMappings",
          "(Ljava/lang/Class;Ljava/util/Map;)Ljava/lang/String;", false);
      mv.visitMethodInsn(INVOKEINTERFACE, "org/apache/commons/logging/Log", "trace", "(Ljava/lang/Object;)V", true);
      Label l4 = new Label();
      mv.visitJumpInsn(GOTO, l4);
      mv.visitLabel(l3);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "mappingsLogger", "Lorg/apache/commons/logging/Log;");
      mv.visitMethodInsn(INVOKEINTERFACE, "org/apache/commons/logging/Log", "isDebugEnabled", "()Z", true);
      mv.visitJumpInsn(IFEQ, l4);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "mappingsLogger", "Lorg/apache/commons/logging/Log;");
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 3);
      mv.visitVarInsn(ALOAD, 4);
      mv.visitMethodInsn(INVOKESPECIAL, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "formatMappings",
          "(Ljava/lang/Class;Ljava/util/Map;)Ljava/lang/String;", false);
      mv.visitMethodInsn(INVOKEINTERFACE, "org/apache/commons/logging/Log", "debug", "(Ljava/lang/Object;)V", true);
      mv.visitLabel(l4);
      mv.visitVarInsn(ALOAD, 4);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 3);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitInvokeDynamicInsn("accept",
          "(Lorg/springframework/web/servlet/handler/AbstractHandlerMethodMapping;Ljava/lang/Class;Ljava/lang/Object;)Ljava/util/function/BiConsumer;",
          new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
              "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;"
                  + "Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"),
          new Object[]{Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)V"),
              new Handle(Opcodes.H_INVOKESPECIAL, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "lambda$updateHandlerMethods$2",
                  "(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/Object;)V"), Type.getType("(Ljava/lang/reflect/Method;Ljava/lang/Object;)V")});
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "forEach", "(Ljava/util/function/BiConsumer;)V", true);
      mv.visitLabel(l2);
      mv.visitInsn(RETURN);
      mv.visitMaxs(4, 5);
      mv.visitEnd();
    }

    {
      MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_SYNTHETIC, "lambda$updateHandlerMethods$2",
          "(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/Object;)V"
          , null, null);
      mv.visitParameter("userType", ACC_FINAL + ACC_SYNTHETIC);
      mv.visitParameter("handler", ACC_FINAL + ACC_SYNTHETIC);
      mv.visitParameter("method", ACC_SYNTHETIC);
      mv.visitParameter("mapping", ACC_SYNTHETIC);
      mv.visitCode();
      mv.visitVarInsn(ALOAD, 3);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKESTATIC, "org/springframework/aop/support/AopUtils", "selectInvocableMethod",
          "(Ljava/lang/reflect/Method;Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
      mv.visitVarInsn(ASTORE, 5);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 2);
      mv.visitVarInsn(ALOAD, 5);
      mv.visitVarInsn(ALOAD, 4);
      mv.visitMethodInsn(INVOKEVIRTUAL, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "registerHandlerMethod",
          "(Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/Object;)V", false);
      mv.visitInsn(RETURN);
      mv.visitMaxs(4, 6);
      mv.visitEnd();
    }
    {
      MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_SYNTHETIC, "lambda$updateHandlerMethods$1", "(Ljava/lang/Class;Ljava/lang/reflect/Method;)Ljava/lang/Object;", null
          , null);
      mv.visitParameter("userType", ACC_FINAL + ACC_SYNTHETIC);
      mv.visitParameter("method", ACC_SYNTHETIC);
      mv.visitCode();
      Label l0 = new Label();
      Label l1 = new Label();
      Label l2 = new Label();
      mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
      mv.visitLabel(l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 2);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKEVIRTUAL, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "getMappingForMethod",
          "(Ljava/lang/reflect/Method;Ljava/lang/Class;)Ljava/lang/Object;", false);
      mv.visitLabel(l1);
      mv.visitInsn(ARETURN);
      mv.visitLabel(l2);
      mv.visitVarInsn(ASTORE, 3);
      mv.visitTypeInsn(NEW, "java/lang/IllegalStateException");
      mv.visitInsn(DUP);
      mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
      mv.visitLdcInsn("Invalid mapping on handler class [");
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
      mv.visitLdcInsn("]: ");
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
      mv.visitVarInsn(ALOAD, 2);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
      mv.visitVarInsn(ALOAD, 3);
      mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalStateException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
      mv.visitInsn(ATHROW);
      mv.visitMaxs(4, 4);
      mv.visitEnd();
    }

    super.beforeTransform();
  }
}
