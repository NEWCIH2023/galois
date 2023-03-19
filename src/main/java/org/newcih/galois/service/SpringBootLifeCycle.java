package org.newcih.galois.service;

import java.util.List;
import java.util.function.Consumer;

/**
 * SpringBoot运行周期管理
 */
public class SpringBootLifeCycle {

    private List<Consumer<?>> runners;
    private boolean started;

    private static final SpringBootLifeCycle instance = new SpringBootLifeCycle();

    private SpringBootLifeCycle() {
    }

    public static SpringBootLifeCycle getInstance() {
        return instance;
    }

    public List<Consumer<?>> getRunners() {
        return runners;
    }

    public void setRunners(List<Consumer<?>> runners) {
        this.runners = runners;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;

        if (started) {
            getRunners().forEach(Consumer::accept);
        }
    }
}
