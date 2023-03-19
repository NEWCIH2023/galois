package org.newcih.galois.service;

import org.newcih.galois.utils.GaloisLog;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AfterSpringBootStarted implements ApplicationRunner {
    private static final GaloisLog logger = GaloisLog.getLogger(AfterSpringBootStarted.class);

    @Override
    public void run(ApplicationArguments args) {
        logger.info("lifeCycle detects SpringBoot Started!");
        SpringBootLifeCycle.getInstance().markStarted();
    }
}
