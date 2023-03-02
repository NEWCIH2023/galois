package org.newcih.galois.service.watch;

import org.newcih.galois.utils.SystemUtil;

import java.io.File;

/**
 * project file manager
 */
public class ProjectFileManager {

    private static final ProjectFileManager manager = new ProjectFileManager();
    private String outputPath;
    private String classPath;

    private ProjectFileManager() {
        outputPath = SystemUtil.getOutputPath();
        classPath = outputPath.replace("/", File.separator) + "classes" + File.separator;
    }

    public static ProjectFileManager getInstance() {
        return manager;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

}
