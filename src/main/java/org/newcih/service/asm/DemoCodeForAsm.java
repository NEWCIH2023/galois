package org.newcih.service.asm;

import jdk.internal.org.objectweb.asm.*;

import java.io.IOException;

import static org.objectweb.asm.Opcodes.ASM9;

public class DemoCodeForAsm {

    public static DemoCodeForAsm getInstance() {
        return new DemoCodeForAsm();
    }

    public void testB() {
        long var1 = System.currentTimeMillis();
        System.err.println("========>I am B");
        long var3 = System.currentTimeMillis();
        System.out.println((new StringBuilder()).append("cost:").append(var3 - var1).toString());
    }

    public static void testInspectCode() throws IOException {
        ClassReader cr = new ClassReader("org.newcih.service.asm.DemoCodeForAsm");
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
        cr.accept(new ClassVisitor(ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, final String name, String descriptor, String signature,
                                             String[] exceptions) {
                MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor() {
                    @Override
                    public void visitLineNumber(int i, Label label) {
                        System.out.println("经过这个测试行数：" + i);
                        super.visitLineNumber(i, label);
                    }

                    public void visitParameter(String name, int access){

                    }
                }
            }
        });
    }
}
