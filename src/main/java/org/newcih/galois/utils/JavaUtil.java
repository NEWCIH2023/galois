/*
 * MIT License
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

package org.newcih.galois.utils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class JavaUtil {

    public static final String sourceCodePath = "C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\src\\main" +
            "\\java" +
            "\\org\\newcih\\utils\\JavaUtil.java";

    private static final String tempDir = System.getProperty("java.io.tmpdir");

    private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    //TODO 这个需要后期给每种文件包装一个类，以含有足够信息后才能继续完成，目前关于编译后去哪里获取到class文件仍存在问题
    public static File compile(File file) {
        compiler.run(null, null, null, "-d", tempDir, file.getAbsolutePath());
        return null;
    }

    public static void main(String[] args) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        FileOutputStream fis = new FileOutputStream("./hui");
        compiler.run(null, fis, null, sourceCodePath);
        fis.flush();
        fis.close();
    }
}
