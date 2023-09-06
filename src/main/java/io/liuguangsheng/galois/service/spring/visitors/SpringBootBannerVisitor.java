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

import io.liuguangsheng.galois.constants.ClassNameConstant;
import io.liuguangsheng.galois.constants.Constant;
import io.liuguangsheng.galois.service.MethodAdapter;
import io.liuguangsheng.galois.service.annotation.AsmVisitor;
import io.liuguangsheng.galois.service.spring.BannerService;
import io.liuguangsheng.galois.service.spring.SpringAgentService;
import io.liuguangsheng.galois.service.spring.SpringBeanReloader;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.springframework.boot.SpringApplicationRunListener;

import java.util.List;
import java.util.Objects;

import static io.liuguangsheng.galois.constants.ClassNameConstant.CLASS_SPRING_BOOT_BANNER;
import static io.liuguangsheng.galois.constants.Constant.DOT;
import static io.liuguangsheng.galois.constants.Constant.SLASH;
import static jdk.internal.org.objectweb.asm.Opcodes.*;

@AsmVisitor(value = "SpringBootBannerVisitor", manager = SpringAgentService.class)
public class SpringBootBannerVisitor extends MethodAdapter {
    /**
     * Instantiates a new Method adapter.
     *
     * @param className the class name
     */
    protected SpringBootBannerVisitor(String className) {
        super(className);
    }

    public SpringBootBannerVisitor() {
        super(CLASS_SPRING_BOOT_BANNER);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                     String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        if (Objects.equals("printBanner", name)) {
            return new PrintBannerMethodVisitor(ASM5, mv);
        }

        return mv;
    }

    public interface NecessaryMethods {
        void printBanner();
    }

    static class PrintBannerMethodVisitor extends MethodVisitor {

        public PrintBannerMethodVisitor(int i, MethodVisitor methodVisitor) {
            super(i, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int i, String s, String s1, String s2, boolean b) {
            if ("println".equals(s1) && "()V".equals(s2)) {
                String pClassName = BannerService.class.getName().replace(DOT, SLASH);

                mv.visitCode();
                mv.visitTypeInsn(NEW, pClassName);
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, pClassName, "<init>", "()V", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, pClassName, "printBanner", "()V", false);
            }

            super.visitMethodInsn(i, s, s1, s2, b);
        }
    }
}
