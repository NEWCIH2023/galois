package io.liuguangsheng.galois.service.spring.listeners;

import io.liuguangsheng.galois.utils.GaloisLog;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;

/**
 * The type Java source manager.
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class ClassModifyRecorder {

  private final Set<String> changedClassNames = new HashSet<>(1 << 10);

  private static final ClassModifyRecorder instance = new ClassModifyRecorder();
  private static final Logger logger = new GaloisLog(ClassModifyRecorder.class);

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static ClassModifyRecorder getInstance() {
    return instance;
  }

  /**
   * Add changed class name.
   *
   * @param className the class name
   */
  public void addClassName(String className) {
    if (logger.isDebugEnabled()) {
      logger.debug("记录到类{}源码发生变动.", className);
    }

    changedClassNames.add(className);
  }

  /**
   * Class done.
   *
   * @param className the class name
   */
  public boolean removeClassName(String className) {
    if (logger.isDebugEnabled()) {
      logger.debug("类{}的源码变动已被处理.", className);
    }

    return changedClassNames.remove(className);
  }

  /**
   * Clear class name.
   */
  public void clearClassName() {
    if (logger.isDebugEnabled()) {
      logger.debug("已清空类源码变动缓存.");
    }

    changedClassNames.clear();
  }

}
