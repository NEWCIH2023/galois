package org.newcih.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class CaptureStorage {

    public static final String GENERATED_INSERT_METHOD_POSTFIX = "$$$capture";
    private static final ReferenceQueue<?> KEY_REFERENCE_QUEUE = new ReferenceQueue();
    private static final ConcurrentMap<WeakReference, CapturedStack> STORAGE = new ConcurrentHashMap<>();

    private static final ThreadLocal<Deque<CaptureStorage>> CURRENT_STACKS = new ThreadLocal<Deque<CaptureStorage>>() {
        protected Deque<CaptureStorage> initialValue() {
            return new LinkedList<>();
        }
    };

    public static void setEnabled(boolean enabled) {
        ENABLED = enabled;
    }

    public static boolean DEBUG;
    private static boolean ENABLED = true;

    public CaptureStorage() {
    }

    public static void capture(Object key) {
        if (ENABLED) {
            try {
                Throwable exception = new Throwable();
                if (DEBUG) {
                    System.out.println("capture " + getCallerDescriptor(exception) + " - " + key);
                }

                CapturedStack stack = createCapturedStack(exception, (CapturedStack) ((Deque) CURRENT_STACKS.get()).peekLast());
                processQueue();
                WeakKey keyRef = new WeakKey(key, KEY_REFERENCE_QUEUE);
                STORAGE.put(keyRef, stack);
            } catch (Exception var4) {
                handleException(var4);
            }
        }
    }

    public static void insertEnter(Object key) {
        if (ENABLED) {
            try {
                CapturedStack stack = STORAGE.get(new HardKey(key));
                Deque<CapturedStack> currentStacks = (Deque) CURRENT_STACKS.get();
                currentStacks.add(stack);

                if (DEBUG) {
                    System.out.println("insert " + getCallerDescriptor(new Throwable()) + " -> " + key + ", stack savd (" + currentStacks.size() + ")");
                }
            } catch (Exception var3) {
                handleException(var3);
            }
        }
    }

    public static void insertExit(Object key) {
        if (ENABLED) {
            try {
                Deque<CaptureStorage> currentStacks = CURRENT_STACKS.get();
                currentStacks.removeLast();
                if (DEBUG) {
                    System.err.println("insert " + getCallerDescriptor(new Throwable()) + " <- " + key + ", stack removed (" + currentStacks.size() + ")");
                }
            } catch (Exception var2) {
                handleException(var2);
            }
        }
    }

    private static void processQueue() {
        WeakKey key;
        while ((key = (WeakKey) KEY_REFERENCE_QUEUE.poll()) != null) {
            STORAGE.remove(key);
        }
    }

    private static CapturedStack createCapturedStack(Throwable exception, CapturedStack insertMatch) {
        if (insertMatch != null) {
            CapturedStack stack = new DeepCapturedStack(exception, insertMatch);
            if (((CapturedStack) stack).getRecursionDepth() > 100) {
                ArrayList<StackTraceElement> trace = getStackTrace((CapturedStack) stack, 500);
                trace.trimToSize();
                stack = new UnwindCapturedStack(trace);
            }

            return (CapturedStack) stack;
        } else {
            return new ExceptionCapturedStack(exception);
        }
    }

    public static String getCurrentCapturedStack(int limit) throws IOException {
        return wrapInString((CapturedStack) ((Deque) CURRENT_STACKS.get()).peekLast(), limit);
    }

    private static String wrapInString(CapturedStack stack, int limit) throws IOException {
        if (stack == null) {
            return null;
        } else {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            Iterator var4 = getStackTrace(stack, limit).iterator();

            while (var4.hasNext()) {
                StackTraceElement elem = (StackTraceElement) var4.next();
                if (elem == null) {
                    dos.writeBoolean(false);
                } else {
                    dos.writeBoolean(true);
                    dos.writeUTF(elem.getClassName());
                    dos.writeUTF(elem.getMethodName());
                    dos.writeInt(elem.getLineNumber());
                }
            }

            return bas.toString("ISO-8859-1");
        }
    }

    private static Object[][] wrapInArray(CapturedStack stack, int limit) {
        if (stack == null) {
            return null;
        } else {
            List<StackTraceElement> stackTrace = getStackTrace(stack, limit);
            Object[][] res = new Object[stackTrace.size()][];

            for (int i = 0; i < stackTrace.size(); i++) {
                StackTraceElement elem = stackTrace.get(i);
                if (elem == null) {
                    res[i] = null;
                } else {
                    res[i] = new Object[]{elem.getClassName(), elem.getFileName(), elem.getMethodName(), String.valueOf(elem.getLineNumber())};
                }
            }

            return res;
        }
    }

    private static ArrayList<StackTraceElement> getStackTrace(CapturedStack stack, int limit) {
        ArrayList<StackTraceElement> res = new ArrayList<>();

        while (stack != null && res.size() <= limit) {
            List<StackTraceElement> stackTraces = stack.getStackTrace();
            if (stack instanceof DeepCapturedStack) {
                int depth = 0;
                int size;

                for (size = stackTraces.size(); depth < size && !((StackTraceElement) stackTraces.get(depth)).getMethodName().endsWith("$$$capture"); ++depth) {
                }

                int newEnd = depth + 2;
                if (newEnd > size) {
                    stack = null;
                } else {
                    stackTraces = stackTraces.subList(0, newEnd);
                    stack = ((DeepCapturedStack) stack).myInsertMatch;
                }
            } else {
                stack = null;
            }

            res.addAll(stackTraces);
            if (stack != null) {
                res.add((StackTraceElement) null);
            }
        }

        return res;
    }


    private static void handleException(Throwable e) {
        ENABLED = false;
        System.err.println("Critical error in IDEA Async Stacktraces instrumenting agent. Agent is now disabled. Please report to IDEA supprt");
        e.printStackTrace();
    }

    private static String getCallerDescriptor(Throwable e) {
        StackTraceElement caller = e.getStackTrace()[1];
        return caller.getClassName() + "." + caller.getMethodName();
    }

    private static class DeepCapturedStack extends ExceptionCapturedStack {
        final CapturedStack myInsertMatch;
        final int myRecursionDepth;

        DeepCapturedStack(Throwable exception, CapturedStack insertMatch) {
            super(exception);
            this.myInsertMatch = insertMatch;
            this.myRecursionDepth = insertMatch.getRecursionDepth() + 1;
        }

        public int getRecursionDepth() {
            return this.myRecursionDepth;
        }
    }

    private static class ExceptionCapturedStack implements CapturedStack {
        final Throwable myException;

        private ExceptionCapturedStack(Throwable exception) {
            this.myException = exception;
        }

        public List<StackTraceElement> getStackTrace() {
            StackTraceElement[] stackTrace = this.myException.getStackTrace();
            return Arrays.asList(stackTrace).subList(1, stackTrace.length);
        }

        public int getRecursionDepth() {
            return 0;
        }
    }

    private static class UnwindCapturedStack implements CapturedStack {
        final List<StackTraceElement> myStackTraceElements;

        UnwindCapturedStack(List<StackTraceElement> elements) {
            this.myStackTraceElements = elements;
        }

        public List<StackTraceElement> getStackTrace() {
            return this.myStackTraceElements;
        }

        public int getRecursionDepth() {
            return 0;
        }
    }

    private interface CapturedStack {
        List<StackTraceElement> getStackTrace();

        int getRecursionDepth();
    }

    private static class WeakKey extends WeakReference {
        private final int myHash;

        WeakKey(Object key, ReferenceQueue q) {
            super(key, q);
            this.myHash = System.identityHashCode(key);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (!(o instanceof WeakKey)) {
                return false;
            } else {
                Object t = this.get();
                Object u = ((WeakKey) o).get();

                if (t != null && u != null) {
                    return t == u;
                } else {
                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            return this.myHash;
        }
    }

    private static class HardKey {
        private final Object myKey;
        private final int myHash;

        HardKey(Object key) {
            this.myKey = key;
            this.myHash = System.identityHashCode(key);
        }

        public boolean equals(Object o) {
            return this == o || o instanceof WeakKey && ((WeakKey) o).get() == this.myKey;
        }

        public int hashCode() {
            return this.myHash;
        }
    }

}
