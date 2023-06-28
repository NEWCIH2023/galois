/*
 * MIT License
 *
 * Copyright (c) [2023] [liuguangsheng]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.liuguangsheng.galois.service.spring.visitors;

import io.liuguangsheng.galois.service.MethodAdapter;
import io.liuguangsheng.galois.service.annotation.AsmVisitor;
import io.liuguangsheng.galois.service.spring.SpringAgentService;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;

import static io.liuguangsheng.galois.constants.ClassNameConstant.ABSTRACT_HANDLER_METHOD_MAPPING;
import static jdk.internal.org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static jdk.internal.org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static jdk.internal.org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static jdk.internal.org.objectweb.asm.Opcodes.ALOAD;
import static jdk.internal.org.objectweb.asm.Opcodes.ARETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.ASTORE;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static jdk.internal.org.objectweb.asm.Opcodes.RETURN;

/**
 * The type Handler method mapping visitor.
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
@AsmVisitor(value = "HandlerMethodMappingVisitor", manager = SpringAgentService.class)
public class HandlerMethodMappingVisitor extends MethodAdapter {
	
	public static final String UPDATE_HANDLER_METHODS = "updateHandlerMethods";
	
	/**
	 * Instantiates a new Method adapter.
	 */
	public HandlerMethodMappingVisitor() {
		super(ABSTRACT_HANDLER_METHOD_MAPPING);
	}
	
	@Override
	public void visitEnd() {
		{
			MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, UPDATE_HANDLER_METHODS, "(Ljava/lang/Object;)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
			mv.visitMethodInsn(INVOKESTATIC, "org/springframework/util/ClassUtils", "getUserClass", "(Ljava/lang/Class;)Ljava/lang/Class;", false);
			mv.visitVarInsn(ASTORE, 2);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInvokeDynamicInsn("inspect",
					"(Lorg/springframework/web/servlet/handler/AbstractHandlerMethodMapping;Ljava/lang/Class;)Lorg/springframework/core/MethodIntrospector$MetadataLookup;",
					new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
							"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;"
									+ "Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"),
					new Object[]{Type.getType("(Ljava/lang/reflect/Method;)Ljava/lang/Object;"),
							new Handle(Opcodes.H_INVOKESPECIAL, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "lambda$updateHandlerMethods$2",
									"(Ljava/lang/Class;Ljava/lang/reflect/Method;)Ljava/lang/Object;"), Type.getType("(Ljava/lang/reflect/Method;)Ljava/lang/Object;")});
			mv.visitMethodInsn(INVOKESTATIC, "org/springframework/core/MethodIntrospector", "selectMethods",
					"(Ljava/lang/Class;Lorg/springframework/core/MethodIntrospector$MetadataLookup;)Ljava/util/Map;", false);
			mv.visitVarInsn(ASTORE, 3);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInvokeDynamicInsn("accept",
					"(Lorg/springframework/web/servlet/handler/AbstractHandlerMethodMapping;Ljava/lang/Class;Ljava/lang/Object;)Ljava/util/function/BiConsumer;",
					new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
							"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;"
									+ "Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"),
					new Object[]{Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)V"),
							new Handle(Opcodes.H_INVOKESPECIAL, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "lambda$updateHandlerMethods$3",
									"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/Object;)V"),
							Type.getType("(Ljava/lang/reflect/Method;Ljava/lang/Object;)V")});
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "forEach", "(Ljava/util/function/BiConsumer;)V", true);
			mv.visitInsn(RETURN);
			mv.visitMaxs(4, 4);
			mv.visitEnd();
		}
		{
			MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_SYNTHETIC, "lambda$updateHandlerMethods$3",
					"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/Object;)V",
					null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESTATIC, "org/springframework/aop/support/AopUtils", "selectInvocableMethod",
					"(Ljava/lang/reflect/Method;Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
			mv.visitVarInsn(ASTORE, 5);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 4);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "unregisterMapping", "(Ljava/lang/Object;)V", false);
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
			MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_SYNTHETIC, "lambda$updateHandlerMethods$2", "(Ljava/lang/Class;Ljava/lang/reflect/Method;)Ljava/lang/Object;",
					null,
					null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/springframework/web/servlet/handler/AbstractHandlerMethodMapping", "getMappingForMethod",
					"(Ljava/lang/reflect/Method;Ljava/lang/Class;)Ljava/lang/Object;", false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		
	}
}
