package io.liuguangsheng.galois.service.spring.listeners;

import static io.liuguangsheng.galois.constants.FileType.JAVA_FILE;
import io.liuguangsheng.galois.service.annotation.LazyBean;
import io.liuguangsheng.galois.service.monitor.FileChangedListener;
import io.liuguangsheng.galois.service.spring.SpringAgentService;
import io.liuguangsheng.galois.utils.ClassUtil;
import io.liuguangsheng.galois.utils.FileUtil;
import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author liuguangsheng
 * @since 1.0.0
 **/
@LazyBean(value = "JavaFileListener", manager = SpringAgentService.class, rank = 1)
public class JavaFileListener implements FileChangedListener {

  public static final Set<String> javaFileChangedList = new HashSet<>(1000);

  /**
   * is listener useful for this file object
   *
   * @param file the changed file
   * @return is the listener monitor this file change
   */
  @Override
  public boolean isUseful(File file) {
    return Objects.equals(FileUtil.getFileType(file), JAVA_FILE.getFileType());
  }

  public void printJavaFileChangedList() {
    String fileNames = javaFileChangedList.stream().collect(Collectors.joining(","));
    System.out.println("当前Java源码变更列表：" + fileNames);
  }

  /**
   * handler for file created
   *
   * @param file the changed file
   */
  @Override
  public void createdHandle(File file) {
    String className = ClassUtil.getClassNameFromSource(file);
    javaFileChangedList.add(className);
    printJavaFileChangedList();
  }

  /**
   * handler for file modifed
   *
   * @param file the changed file
   */
  @Override
  public void modifiedHandle(File file) {
    String className = ClassUtil.getClassNameFromSource(file);
    javaFileChangedList.add(className);
    printJavaFileChangedList();
  }

  /**
   * handler for file deleted
   *
   * @param file the changed file
   */
  @Override
  public void deletedHandle(File file) {

  }
}
