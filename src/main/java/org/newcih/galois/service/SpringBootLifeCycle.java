package org.newcih.galois.service;

import java.util.List;
import java.util.function.Consumer;

/**
 * SpringBoot运行周期管理
 */
public class SpringBootLifeCycle {

    private List<Consumer<?>> runner;
    /**
     * the following value of program running state
     * <p>
     * -1: default
     * 0: starting
     * 1: started
     */
    private int runState = -1;
    public static final int STARTING = 0;
    public static final int STARTED = 1;

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
