package org.newcih.service;

import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import com.sun.tools.javac.code.Attribute;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class CaptureAgent {

    private static Instrumentation ourInstrumentation;

    private static final Set<Class<?>> mySkipped = new HashSet<>();

    private static final Map<String, List<InstrumentPoint>> myInstrumentPoints = new HashMap<>();

    private static final KeyProvider FIRST_PARAM = param(0);

    static final KeyProvider THIS_KEY_PROVIDER = new KeyProvider() {
        @Override
        public void loadKey(MethodVisitor mv, boolean isStatic, Type[] argumentTypes, String methodDisplayName, CaptureInstrumentor instrumentor) {
            if (isStatic) {
                throw new IllegalStateException("This is not available in a static method " + methodDisplayName);
            } else {
                mv.visitVarInsn(25, 0);
            }
        }
    };

    public static final String CONSTRUCTOR = "<init>";

    public CaptureAgent() {
    }

    public static void premain(String args, Instrumentation instrumentation) {
        if (System.getProperty("intellij.debug.agent") != null) {
            System.err.println("Capture agent: more than one agent is not allowed, skipping");
        } else {
            System.setProperty("intellij.debug.agent", "true");
            ourInstrumentation = instrumentation;

            try {
                readSettings(args);
            }
        }
    }

    private static void readSettings(String uri) {
        if (uri != null && !uri.isEmpty()) {
            Properties properties = new Properties();
            File file;

            try {
                InputStream stream = null;

                try {
                    file = new File(new URI(uri));
                    stream = new FileInputStream(file);
                    properties.load(stream);
                } catch (URISyntaxException | IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            } catch (Exception var8) {
                System.out.println("Capture agent: unable to read settings");
                var8.printStackTrace();
                return;
            }

            if (Boolean.parseBoolean(properties.getProperty("disabled", "false"))) {
                CaptureStorage.setEnabled(false);
            }

            Iterator var9 = properties.entrySet().iterator();
            while (var9.hasNext()) {
                Map.Entry<Object, Object> entry = (Map.Entry) var9.next();

            }
        }
    }

    private static InstrumentPoint addPoint(String propertyKey, String propertyValue) {
        if (propertyKey.startsWith("capture")) {
            return addPoint(true, propertyValue);
        } else {
            return propertyKey.startsWith("insert") ? addPoint(false, propertyValue) : null;
        }
    }

    private static InstrumentPoint addPoint(boolean capture, String line) {
        String[] split = line.split(" ");
//        KeyProvider keyProvider = crea
    }

    private static KeyProvider param(int idx) {
        return new ParamKeyProvider(idx);
    }

    private static class ParamKeyProvider implements KeyProvider {
        private final int myIdx;

        ParamKeyProvider(int idx) {
            this.myIdx = idx;
        }

        public void loadKey(MethodVisitor mv, boolean isStatic, Type[] argumentTypes, String methodDisplayName, CaptureInstrumentor instrumentor) {
            int index = isStatic ? 0 : 1;
            if (this.myIdx >= argumentTypes.length) {
                throw new IllegalStateException("Argumnet with id " + this.myIdx + " is not available, method " + methodDisplayName + " has only " + argumentTypes.length);
            } else {
                int sort = argumentTypes[this.myIdx].getSort();
                if (sort != 10 && sort != 9) {
                    throw new IllegalStateException("Argument with id " + this.myIdx + " in method " + methodDisplayName + " must be an object");
                } else {
                    for (int i = 0; i < this.myIdx; i++) {
                        index += argumentTypes[i].getSize();
                    }

                    mv.visitVarInsn(25, index);
                }
            }
        }
    }

    private static class InstrumentPoint {
        static final String ANY_DESC = "*";
        final boolean myCapture;
        final String myClassName;
        final String myMethodName;
        final String myMethodDesc;
        final KeyProvider myKeyProvider;

        InstrumentPoint(boolean capture, String className, String methodName, String methodDesc, KeyProvider keyProvider) {
            this.myCapture = capture;
            this.myClassName = className;
            this.myMethodName = methodName;
            this.myMethodDesc = methodDesc;
            this.myKeyProvider = keyProvider;
        }

        boolean matchesMethod(String name, String desc) {
            if (!this.myMethodName.equals(name)) {
                return false;
            } else {
                return this.myMethodDesc.equals("*") || this.myMethodDesc.equals(desc);
            }
        }
    }

    private interface KeyProvider {
        void loadKey(MethodVisitor var1, boolean var2, Type[] var3, String var4, CaptureInstrumentor var5);
    }

    private static class CaptureInstrumentor extends ClassVisitor {

        private final List<? extends InstrumentPoint> myInstrumentPoints;
        private final Map<String, String> myFields = new HashMap<>();
        private String mySuperName;
        private boolean myIsInterface;

        public CaptureInstrumentor(int api, ClassVisitor cv, List<? extends InstrumentPoint> instrumentPoints) {
            super(api, cv);
            this.myInstrumentPoints = instrumentPoints;
        }

        private static String getNewName(String name) {
            return name + "$$$capture";
        }

        private static String getMethodDisplayName(String className, String methodName, String desc) {
            return className + "." + methodName + desc;
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.mySuperName = superName;
            this.myIsInterface = (access & 512) != 0;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            this.myFields.put(name, desc);
            return super.visitField(access, name, desc, signature, value);
        }

        public MethodVisitor visitMethod(final int access, String name, final String desc, String signature, String[] exceptions) {
            if ((access & 64) == 0) {
                Iterator var6 = this.myInstrumentPoints.iterator();

                while (var6.hasNext()) {
                    final InstrumentPoint point = (InstrumentPoint) var6.next();
                    if (point.matchesMethod(name, desc)) {
                        final String methodDisplayName = getMethodDisplayName(point.myClassName, name, desc);
                        if (CaptureStorage.DEBUG) {
                            System.out.println("Capture agent: instrumented " + (point.myCapture ? "capture" : "insert") + " point at " + methodDisplayName);
                        }

                        if (point.myCapture) {
                            if ("<init>".equals(name) && point.myKeyProvider == CaptureAgent.THIS_KEY_PROVIDER)
                        }
                    }
                }
            }
        }

    }

}
