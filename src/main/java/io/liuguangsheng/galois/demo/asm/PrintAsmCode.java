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

package io.liuguangsheng.galois.demo.asm;

import io.liuguangsheng.galois.utils.StringUtil;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.util.ASMifier;
import jdk.internal.org.objectweb.asm.util.Printer;
import jdk.internal.org.objectweb.asm.util.Textifier;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * The type Print asm code.
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class PrintAsmCode {
	
	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 * @throws IOException the io exception
	 */
	public static void main(String[] args) throws IOException {
		printCode(AbstractHandlerMethodMapping.class.getName(), true);
	}
	
	/**
	 * Print code.
	 *
	 * @param className the class name
	 * @param asmCode   the asm code
	 * @throws IOException the io exception
	 */
	public static void printCode(String className, boolean asmCode) throws IOException {
		if (StringUtil.isBlank(className)) {
			className = PrintAsmCode.class.getName();
		}
		
		ClassReader cr = new ClassReader(className);
		Printer printer = asmCode ? new ASMifier() : new Textifier();
		PrintWriter printWriter = new PrintWriter(System.out, true);
		TraceClassVisitor cv = new TraceClassVisitor(null, printer, printWriter);
		cr.accept(cv, ClassReader.SKIP_FRAMES + ClassReader.SKIP_DEBUG);
	}
	
	public void updateHandlerMethods(Object handler) {
	}
	
	protected void detectHandlerMethods(Object handler) {
	
	}
}
