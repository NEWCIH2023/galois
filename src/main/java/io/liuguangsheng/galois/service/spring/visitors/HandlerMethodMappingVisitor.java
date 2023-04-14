package io.liuguangsheng.galois.service.spring.visitors;

import io.liuguangsheng.galois.constants.ClassNameConstant;
import io.liuguangsheng.galois.service.MethodAdapter;
import io.liuguangsheng.galois.service.annotation.AsmVisitor;
import io.liuguangsheng.galois.service.spring.SpringAgentService;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import static io.liuguangsheng.galois.constants.Constant.DOT;
import static io.liuguangsheng.galois.constants.Constant.SLASH;
import static jdk.internal.org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static jdk.internal.org.objectweb.asm.Opcodes.ALOAD;
import static jdk.internal.org.objectweb.asm.Opcodes.ASTORE;
import static jdk.internal.org.objectweb.asm.Opcodes.CHECKCAST;
import static jdk.internal.org.objectweb.asm.Opcodes.DUP;
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
        String slashClassName = className.replace(DOT, SLASH);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "updateHandlerMethods", "(Ljava/lang/Object;)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(235, l0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(INSTANCEOF, "java/lang/String");
        Label l1 = new Label();
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitVarInsn(ALOAD, 0);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLineNumber(236, l2);
        mv.visitMethodInsn(INVOKEVIRTUAL,
                slashClassName,
                "obtainApplicationContext",
                "()Lorg/springframework/context/ApplicationContext;",
                false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEINTERFACE,
                "org/springframework/context/ApplicationContext",
                "getType",
                "(Ljava/lang/String;)Ljava/lang/Class;",
                true);
        Label l3 = new Label();
        mv.visitJumpInsn(GOTO, l3);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        mv.visitLabel(l3);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Class"});
        mv.visitVarInsn(ASTORE, 2);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLineNumber(238, l4);
        mv.visitVarInsn(ALOAD, 2);
        Label l5 = new Label();
        mv.visitJumpInsn(IFNULL, l5);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLineNumber(239, l6);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC,
                "org/springframework/util/ClassUtils",
                "getUserClass",
                "(Ljava/lang/Class;)Ljava/lang/Class;",
                false);
        mv.visitVarInsn(ASTORE, 3);
        Label l7 = new Label();
        mv.visitLabel(l7);
        mv.visitLineNumber(240, l7);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitTypeInsn(NEW, slashClassName + "$1");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKESPECIAL,
                slashClassName + "$1",
                "<init>",
                "(L" + slashClassName + ";Ljava/lang/Class;)V",
                false);
        mv.visitMethodInsn(INVOKESTATIC,
                "org/springframework/core/MethodIntrospector",
                "selectMethods",
                "(Ljava/lang/Class;Lorg/springframework/core/MethodIntrospector$MetadataLookup;)Ljava/util/Map;",
                false);
        mv.visitVarInsn(ASTORE, 4);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitLineNumber(251, l8);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitTypeInsn(NEW, slashClassName + "$2");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL,
                slashClassName + "$2",
                "<init>",
                "(L" + slashClassName + ";Ljava/lang/Class;Ljava/lang/Object;)V",
                false);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "forEach", "(Ljava/util/function/BiConsumer;)V", true);
        mv.visitLabel(l5);
        mv.visitLineNumber(259, l5);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/Class"}, 0, null);
        mv.visitInsn(RETURN);
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitLocalVariable("userType", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l7, l5, 3);
        mv.visitLocalVariable("methods",
                "Ljava/util/Map;",
                "Ljava/util/Map<Ljava/lang/reflect/Method;TT;>;",
                l8,
                l5,
                4);
        mv.visitLocalVariable("this",
                "L" + slashClassName + ";",
                "L" + slashClassName + "<TT;>;",
                l0,
                l9,
                0);
        mv.visitLocalVariable("handler", "Ljava/lang/Object;", null, l0, l9, 1);
        mv.visitLocalVariable("handlerType", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l4, l9, 2);
        mv.visitMaxs(6, 5);
        mv.visitEnd();
    }
}
