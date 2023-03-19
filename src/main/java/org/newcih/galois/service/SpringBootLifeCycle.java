package org.newcih.galois.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.newcih.galois.service.agent.spring.SpringBeanReloader;
import org.springframework.context.ApplicationContext;

/**
 * SpringBoot运行周期管理
 */
public class SpringBootLifeCycle {

    private final List<Consumer<ApplicationContext>> runners = new ArrayList<>(8);
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
    public void addRunner(Consumer<ApplicationContext> runner) {
        if (runner != null) {
            this.runners.add(runner);
        }
    }

    public List<Consumer<ApplicationContext>> getRunners() {
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
            ApplicationContext context = SpringBeanReloader.getInstance().getContext();
            getRunners().forEach(action -> action.accept(context));
        }
    }
}
