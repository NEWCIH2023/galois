package org.newcih.galois.service.demo.asm;

import org.apache.commons.io.IOUtils;
import org.newcih.galois.service.agent.frame.spring.SpringBeanReloader;
import org.objectweb.asm.*;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

public class DemoCodeForAsm {

    private final Object configuration = null;

    public static DemoCodeForAsm getInstance() {
        return new DemoCodeForAsm();
    }

    public static void main(String[] args) throws IOException {
        testInspectCode();
    }

    public static void testInspectCode() throws IOException {
        ClassReader cr = new ClassReader("org.newcih.service.asm.DemoCodeForAsm");
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
        cr.accept(new ClassVisitor(ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, final String name, String descriptor, String signature,
                                             String[] exceptions) {
                MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(ASM9, mv) {
                    @Override
                    public void visitLineNumber(int i, Label label) {
                        System.out.println("经过这个测试行数：" + i);
                        super.visitLineNumber(i, label);
                    }

                    @Override
                    public void visitParameter(String name, int access) {
                        super.visitParameter(name, access);
                    }

                    public void visitInsn(int opcode) {
                        if (!"<init>".equals(name) && (opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                            mv.visitIntInsn(LSTORE, 3);
                            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream");
                            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                            mv.visitInsn(DUP);
                            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                            mv.visitLdcInsn("cost:");
                            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang" +
                                    "/String;)Ljava/lang/StringBuilder", false);
                            mv.visitVarInsn(LLOAD, 3);
                            mv.visitVarInsn(LLOAD, 1);
                            mv.visitInsn(LSUB);
                            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang" +
                                    "/StringBuilder", false);
                            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                                    "()Ljava/lang" + "/String", false);
                            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)"
                                    + "V", false);
                        }

                        mv.visitInsn(opcode);
                    }

                    @Override
                    public void visitCode() {
                        super.visitCode();
                        System.out.println("方法名称==========>" + name);
                        if (!"<init>".equals(name)) {
                            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                            mv.visitIntInsn(LSTORE, 1);
                        }
                    }
                };
            }
        }, 0);

        String path = Objects.requireNonNull(DemoCodeForAsm.class.getResource("")).getPath().substring(1).replace("/"
                , File.separator) + "DemoCodeForAsmTran.class";
        System.out.println("类生成的位置" + path);
        IOUtils.write(cw.toByteArray(), Files.newOutputStream(Paths.get(path)));
    }

    public void printCode() {
        {
            System.out.println("2");
        }
        SpringBeanReloader.getInstance().setApplicationContext((ApplicationContext) this);
        {
            System.out.println(5);
        }
    }

    public void testB() {
        long var1 = System.currentTimeMillis();
        System.err.println("========>I am B");
        long var3 = System.currentTimeMillis();
        System.out.println("cost:" + (var3 - var1));
    }
}
