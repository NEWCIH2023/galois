package org.newcih.service.watch;

import org.newcih.utils.SystemUtil;

import java.io.File;

/**
 * project file manager
 */
public class ProjectFileManager {

    private String outputPath;

    private String classPath;

    private ProjectFileManager() {
        outputPath = SystemUtil.getOutputPath();
        classPath = outputPath.replace("/", File.separator) + "classes" + File.separator;
    }

    private static final ProjectFileManager manager = new ProjectFileManager();

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
