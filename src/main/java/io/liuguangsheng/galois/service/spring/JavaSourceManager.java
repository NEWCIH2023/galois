package io.liuguangsheng.galois.service.spring;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Java source manager.
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class JavaSourceManager {

  private final Set<String> changedClassNames = new HashSet<>(1 << 10);

  private static final JavaSourceManager instance = new JavaSourceManager();

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static JavaSourceManager getInstance() {
    return instance;
  }

  /**
   * Add changed class name.
   *
   * @param className the class name
   */
  public void addClassName(String className) {
    changedClassNames.add(className);
  }

  /**
   * Class done.
   *
   * @param className the class name
   */
  public boolean removeClassName(String className) {
    return changedClassNames.remove(className);
  }

  /**
   * Clear class name.
   */
  public void clearClassName() {
    changedClassNames.clear();
  }

}
