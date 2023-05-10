package io.liuguangsheng.galois.service.monitor;

/**
 * @author liuguangsheng
 * @since 1.0.0
 **/
public interface FileWatchService {

  void init();

  void start();

  void registerListener(FileChangedListener listener);

}
