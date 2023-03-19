package org.newcih.galois.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AfterSpringBootStarted implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        SpringBootLifeCycle lifeCycle = SpringBootLifeCycle.getInstance();
        lifeCycle.setStarted(true);
    }
}
