
/*
 * MIT License
 *
 * Copyright (c) [2023] [$user]
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

package org.newcih.galois.utils;

import static org.newcih.galois.constants.Constant.DOT;
import static org.newcih.galois.constants.Constant.SLASH;
import static org.newcih.galois.constants.FileType.CLASS_FILE;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jdk.internal.org.objectweb.asm.ClassReader;

/**
 * java util
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class JavaUtil {

  private static final String compileDir;
  private static final Pattern packagePattern = Pattern.compile("^package +(\\S+);");
  private static final Pattern classNamePattern = Pattern.compile("class +([\\S&&[^<]]+)");
  /**
   * The constant inst.
   */
  public static Instrumentation instrumentation;

  static {
    compileDir =
        System.getProperty("java.io.tmpdir") + File.separator + "GaloisCompile" + File.separator;
    File directory = new File(compileDir);
    if (!directory.exists()) {
      try {
        directory.mkdir();
      } catch (Exception ignored) {
      }
    }
  }

  /**
   * Gets instrumentation.
   *
   * @return the instrumentation
   */
  public static Instrumentation getInstrumentation() {
    return instrumentation;
  }

  /**
   * Sets instrumentation.
   *
   * @param instrumentation the instrumentation
   */
  public static void setInstrumentation(Instrumentation instrumentation) {
    JavaUtil.instrumentation = instrumentation;
  }

  /**
   * get class name from class
   *
   * @param classFile classFile
   * @return {@link String}
   * @see String
   */
  public static String getClassNameFromClass(File classFile) {
    try {
      ClassReader classReader = new ClassReader(Files.newInputStream(classFile.toPath()));
      return classReader.getClassName().replace(SLASH, DOT);
    } catch (IOException e) {
      return "";
    }
  }

  /**
   * get class name from source
   *
   * @param javaFile javaFile
   * @return {@link String}
   * @see String
   */
  public static String getClassNameFromSource(File javaFile) {
    try (BufferedReader br = new BufferedReader(new FileReader(javaFile))) {
      String tmp = "";
      String result = "";

      Matcher packageMatcher;
      Matcher classNameMatcher;

      while ((tmp = br.readLine()) != null) {
        packageMatcher = packagePattern.matcher(tmp);
        if (packageMatcher.find()) {
          result = packageMatcher.group(1);
        }

        classNameMatcher = classNamePattern.matcher(tmp);
        if (classNameMatcher.find()) {
          result += DOT + classNameMatcher.group(1);
          break;
        }
      }

      return result;
    } catch (Exception e) {
      return null;
    }
  }


  /**
   * Gets class file.
   *
   * @param clazz must include package path
   * @return class file
   */
  public static File getClassFile(Class<?> clazz) {
    return getClassFile(clazz.getName());
  }

  /**
   * Gets class file.
   *
   * @param className must include package path
   * @return class file
   */
  public static File getClassFile(String className) {
    if (StringUtil.isBlank(className)) {
      return null;
    }

    return new File(compileDir + String.join(File.separator, className.split("\\."))
        + CLASS_FILE.getFileType());
  }

  /**
   * compile java source code file to byte[] data
   *
   * @param sourceFile
   * @return
   */
//    public static byte[] compileSource(File sourceFile) {
//        MemoryJavaFileManager manager = new MemoryJavaFileManager(standardJavaFileManager);
//        String sourceCode = FileUtil.readTextFile(sourceFile);
//        JavaFileObject javaFileObject = manager.makeStringSource(sourceFile.getName(), sourceCode);
//        List<JavaFileObject> fileObjectList = Collections.singletonList(javaFileObject);
//        DiagnosticCollector diagnosticCollector = null;
//        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnosticCollector, null, null,
//                fileObjectList);
//        Boolean result = task.call();
//
//        return manager.getClassBytes();
//    }

//    private static class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
//        byte[] classBytes = new byte[0];
//
//        protected MemoryJavaFileManager(JavaFileManager fileManager) {
//            super(fileManager);
//        }
//
//        public byte[] getClassBytes() {
//            return classBytes;
//        }
//
//        @Override
//        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
//                                                   FileObject sibling) throws IOException {
//            if (kind == JavaFileObject.Kind.CLASS) {
//                return new MemoryOutputJavaFileObject(className);
//            } else {
//                return super.getJavaFileForOutput(location, className, kind, sibling);
//            }
//        }
//
//        JavaFileObject makeStringSource(String name, String code) {
//            return new MemoryInputJavaFileObject(name, code);
//        }
//
//        static class MemoryInputJavaFileObject extends SimpleJavaFileObject {
//            final String code;
//
//            protected MemoryInputJavaFileObject(String name, String code) {
//                super(URI.create("string:///" + name), Kind.SOURCE);
//                this.code = code;
//            }
//
//            @Override
//            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
//                return CharBuffer.wrap(code);
//            }
//        }
//
//        class MemoryOutputJavaFileObject extends SimpleJavaFileObject {
//
//            protected MemoryOutputJavaFileObject(String name) {
//                super(URI.create("string:///" + name), Kind.CLASS);
//            }
//
//            @Override
//            public OutputStream openOutputStream() {
//                return new FilterOutputStream(new ByteArrayOutputStream()) {
//                    @Override
//                    public void close() throws IOException {
//                        out.close();
//                        ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
//                        classBytes = bos.toByteArray();
//                    }
//                };
//            }
//        }
//}

}
