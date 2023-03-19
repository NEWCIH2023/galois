package org.newcih.galois.service;

import java.util.List;
import java.util.function.Consumer;

/**
 * SpringBoot运行周期管理
 */
public class SpringBootLifeCycle {

    private List<Consumer<?>> runner;
    private int runState;

    private static final SpringBootLifeCycle instance = new SpringBootLifeCycle();

    private SpringBootLifeCycle() {
    }

    public static SpringBootLifeCycle getInstance() {
        return instance;
    }

    public List<Consumer<?>> getRunner() {
        return runner;
    }

    public void setRunner(List<Consumer<?>> runner) {
        this.runner = runner;
    }

    public int getRunState() {
        return runState;
    }

    public void setRunState(int runState) {
        this.runState = runState;
    }
}
