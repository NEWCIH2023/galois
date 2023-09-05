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

package io.liuguangsheng.galois.service.mybatis.visitors;

import io.liuguangsheng.galois.constants.Constant;
import io.liuguangsheng.galois.service.MethodAdapter;
import io.liuguangsheng.galois.service.annotation.AsmVisitor;
import io.liuguangsheng.galois.service.mybatis.MyBatisAgentService;
import io.liuguangsheng.galois.service.mybatis.MyBatisBeanReloader;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.apache.ibatis.session.Configuration;

import java.util.Objects;

import static io.liuguangsheng.galois.constants.ClassNameConstant.CLASS_MYBATIS_CONFIGURATION;
import static io.liuguangsheng.galois.constants.Constant.DOT;
import static io.liuguangsheng.galois.constants.Constant.SLASH;
import static jdk.internal.org.objectweb.asm.Opcodes.ALOAD;
import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;
import static jdk.internal.org.objectweb.asm.Opcodes.ATHROW;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static jdk.internal.org.objectweb.asm.Opcodes.IRETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.RETURN;

/**
 * mybatis configuration visitor
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
@AsmVisitor(value = "MyBatisConfigurationVisitor", manager = MyBatisAgentService.class)
public class MyBatisConfigurationVisitor extends MethodAdapter {
	
	/**
	 * Instantiates a new My batis configuration visitor.
	 */
	public MyBatisConfigurationVisitor() {
		super(CLASS_MYBATIS_CONFIGURATION);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
	                                 String[] exceptions) {
		MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
		
		if ("<init>".equals(name) && "()V".equals(descriptor)) {
			return new MyBatisConfigurationVisitor.ConstructorVisitor(ASM5, mv);
		}
		
		return mv;
	}
	
	public interface NecessaryMethods {
		
		void setConfiguration(Configuration configuration);
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
				String pClassName = MyBatisBeanReloader.class.getName().replace(DOT, SLASH);
				String vClassName = className.replace(DOT, SLASH);
				
				mv.visitCode();
				mv.visitMethodInsn(INVOKESTATIC, pClassName, "getInstance", "()L" + pClassName + ";", false);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKEVIRTUAL, pClassName, "setConfiguration", "(L" + vClassName + ";)V", false);
				mv.visitInsn(RETURN);
				mv.visitEnd();
			} else {
				super.visitInsn(opcode);
			}
		}
	}
}
