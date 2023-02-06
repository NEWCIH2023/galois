package org.newcih.service;

import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.lang.reflect.Method;

public class AsmDemo extends ClassLoader implements Opcodes {

//    public static void main2(String[] args) throws IOException {
//        String className = "org.newcih.service.ClassExample";
//        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
//        Printer printer = new ASMifier();
//        PrintWriter printWriter = new PrintWriter(System.out, true);
//        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
//        new ClassReader(className).accept(traceClassVisitor, parsingOptions);
//    }

    public static void main(String[] args) throws Exception {
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
        FileOutputStream fos = new FileOutputStream("CopyClassExample.class");
        fos.write(bytes);
        fos.close();

        AsmDemo asmDemo = new AsmDemo();
        Class<?> clazz = asmDemo.defineClass("CopyClassExample", bytes, 0, bytes.length);
        Method[] methods = clazz.getMethods();
        methods[0].invoke(null, new Object[]{null});
    }
}
