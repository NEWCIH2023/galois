package org.newcih.service;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

public class AgentService {

    public static void premain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException {
        System.out.println("premain was called");
        inst.addTransformer(new AgentFormer());
    }

    public static class AgentFormer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            byte[] transformed = null;
            if (!"org/example/App".equals(className)) {
                return null;
            }

            System.out.println("Transforming " + className);
            ClassPool pool = ClassPool.getDefault();
            CtClass cl = null;
            try {
                cl = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
                if (!cl.isInterface()) {
                    cl.addMethod(CtMethod.make("public void p(){System.out.println(\"hello from invoke p method\");}", cl));
                }

                CtMethod[] methods = cl.getMethods();
                for (CtMethod method : methods) {
                    if ("test".equals(method.getName())) {
                        method.setBody("{System.out.println(\"will invoke my test method!\"); p();}");
                        System.out.println("had change this method!");
                    }
                }

                transformed = cl.toBytecode();
            } catch (Exception e) {
                System.err.println("Could not instrument " + className + ", exception : " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (cl != null) {
                    cl.detach();
                }
            }

            return transformed;
        }

        private void doMethod(CtBehavior method) throws NotFoundException, CannotCompileException {
            method.instrument(new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    m.replace("{ long time = System.nanoTime(); $_ = $proceed($$); System.out.println(\"" + m.getClassName() + "." + m.getMethodName() + ":\"+(System.nanoTime()-time));}");
                }
            });
        }
    }
}
