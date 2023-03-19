package org.newcih.galois.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * SpringBoot运行周期管理
 */
public class SpringBootLifeCycle {

    private final List<Consumer<?>> runners = new ArrayList<>(8);
    private int started;

    private static final SpringBootLifeCycle instance = new SpringBootLifeCycle();

    private SpringBootLifeCycle() {
    }

    public static SpringBootLifeCycle getInstance() {
        return instance;
    }

    /**
     * add a new runner
     *
     * @param runner runner
     */
    public void addRunner(Consumer<?> runner) {
        if (runner != null) {
            this.runners.add(runner);
        }
    }

    public List<Consumer<?>> getRunners() {
        return runners;
    }

    /**
     * if spring boot started
     *
     * @return start state
     */
    public boolean isStarted() {
        return started >= 1;
    }

    /**
     * mark spring boot program started
     */
    public void markStarted() {
        this.started++;

        if (started == 1) {
            getRunners().forEach(action -> action.accept(null));
        }
    }
}
