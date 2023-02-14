package org.newcih.service.spring;

import org.newcih.util.GaloisLog;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

public class SpringBeanReload {

    private static final GaloisLog LOGGER = GaloisLog.getLogger(SpringBeanReload.class);

    private ClassPathBeanDefinitionScanner scanner;

    public void registerWatcher(ClassPathBeanDefinitionScanner watcher) {
        scanner = watcher;
    }

    public void addBean(Object bean) {
        LOGGER.info("即将使用%s添加bean<%s>", scanner, bean);
    }

}
