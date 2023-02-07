package org.newcih.service;

import org.objectweb.asm.*;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class AsmDemo extends ClassLoader implements Opcodes {

    public void printCreateClassFileCode() throws IOException {
        String className = "org.newcih.service.ClassExample";
        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
        Printer printer = new ASMifier();
        PrintWriter printWriter = new PrintWriter(System.out, true);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        new ClassReader(className).accept(traceClassVisitor, parsingOptions);
    }

    public byte[] createClassWithoutTestMethod() throws IOException {
        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor;

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "CopyClassExample", null, "java/lang/Object", null);
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        methodVisitor.visitLdcInsn("hello world");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(2, 1);
        methodVisitor.visitEnd();

        classWriter.visitEnd();

        byte[] bytes = classWriter.toByteArray();
        writeClassFile(bytes);

        return bytes;
    }

    public static void writeClassFile(byte[] bytes) throws IOException {
        FileOutputStream fos = new FileOutputStream("target\\classes\\CopyClassExample.class");
        fos.write(bytes);
        fos.close();
    }

    public byte[] createClassWithTestMethod() throws IOException {
        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor;

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "CopyClassExample", null, "java/lang/Object", null);
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        methodVisitor.visitLdcInsn("hello world");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(2, 1);
        methodVisitor.visitEnd();

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "test", "()Ljava/lang/String;", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        methodVisitor.visitLdcInsn("test method");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        methodVisitor.visitLdcInsn("test");
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitMaxs(2, 1);
        methodVisitor.visitEnd();

        classWriter.visitEnd();

        byte[] bytes = classWriter.toByteArray();
        writeClassFile(bytes);

        return bytes;
    }

    public static void main(String[] args) throws Exception {
        AsmDemo asmDemo = new AsmDemo();
        asmDemo.printCreateClassFileCode();
        byte[] bytes = asmDemo.createClassWithTestMethod();
        Class<?> clazz = asmDemo.defineClass("CopyClassExample", bytes, 0, bytes.length);
        Method[] methods = clazz.getMethods();
        methods[0].invoke(null, new Object[]{null});
    }
}
