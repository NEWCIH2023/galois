package org.newcih.utils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class JavaUtil {

    public static final String sourceCodePath = "C:\\Users\\liuguangsheng.SZSYY\\IdeaProjects\\galois\\src\\main\\java" +
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
