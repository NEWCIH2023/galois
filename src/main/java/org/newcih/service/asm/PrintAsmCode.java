package org.newcih.service.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;

public class PrintAsmCode {

    public static void main(String[] args) throws IOException {
        String className = "org.newcih.service.asm.DemoCodeForAsm";

        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
        Printer printer = new ASMifier();
        PrintWriter printWriter = new PrintWriter(System.out, true);
        TraceClassVisitor cv = new TraceClassVisitor(null, printer, printWriter);
        new ClassReader(className).accept(cv, parsingOptions);
    }

}
