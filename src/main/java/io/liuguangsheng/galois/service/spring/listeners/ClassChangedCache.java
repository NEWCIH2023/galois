package io.liuguangsheng.galois.service.spring.listeners;

import io.liuguangsheng.galois.utils.GaloisLog;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.slf4j.Logger;

/**
 * record the changed java file name
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class ClassChangedCache {

  private static final ClassChangedCache instance = new ClassChangedCache();
  private static final Logger logger = new GaloisLog(ClassChangedCache.class);
  private final Set<String> changedClassNames = new CopyOnWriteArraySet<>();

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static ClassChangedCache getInstance() {
    return instance;
  }

  /**
   * Add changed class name.
   *
   * @param className the class name
   */
  public void hadChanged(String className) {
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
  public boolean handleIfExisted(String className) {
    if (logger.isDebugEnabled()) {
      logger.debug("类{}的源码变动已被处理.", className);
    }

    return changedClassNames.remove(className);
  }

  /**
   * Clear class name.
   */
  public void clearCache() {
    if (logger.isDebugEnabled()) {
      logger.debug("已清空类源码变动缓存.");
    }

    changedClassNames.clear();
  }

  public void printCache() {
    if (logger.isDebugEnabled()) {
      logger.debug("当前类源码变动缓存如下");
    }

    changedClassNames.forEach(name -> System.out.print(name + ",\t"));
  }
}
