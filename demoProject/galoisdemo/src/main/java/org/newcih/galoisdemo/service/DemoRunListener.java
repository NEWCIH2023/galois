package org.newcih.galoisdemo.service;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author liuguangsheng
 * @since 1.0.0
 **/
public class DemoRunListener implements SpringApplicationRunListener {
    public DemoRunListener(SpringApplication application, String[] args) {
        System.out.println("DemoRunListener constructor");
    }

    /**
     * Called immediately when the run method has first started. Can be used for very
     * early initialization.
     */
    public void starting() {
        System.out.println("Demo Run Listener Starting.");
    }

    /**
     * Called once the environment has been prepared, but before the
     * {@link ApplicationContext} has been created.
     *
     * @param environment the environment
     */
    public void environmentPrepared(ConfigurableEnvironment environment) {

    }

    /**
     * Called once the {@link ApplicationContext} has been created and prepared, but
     * before sources have been loaded.
     *
     * @param context the application context
     */
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    /**
     * Called once the application context has been loaded but before it has been
     * refreshed.
     *
     * @param context the application context
     */
    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {

    }

    /**
     * The context has been refreshed and the application has started but
     * {@link CommandLineRunner CommandLineRunners} and {@link ApplicationRunner
     * ApplicationRunners} have not been called.
     *
     * @param context the application context.
     * @since 2.0.0
     */
    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println("Demo Run Listener Started. With " + context.getId());
    }

    /**
     * Called immediately before the run method finishes, when the application context has
     * been refreshed and all {@link CommandLineRunner CommandLineRunners} and
     * {@link ApplicationRunner ApplicationRunners} have been called.
     *
     * @param context the application context.
     * @since 2.0.0
     */
    @Override
    public void running(ConfigurableApplicationContext context) {

    }

    /**
     * Called when a failure occurs when running the application.
     *
     * @param context   the application context or {@code null} if a failure occurred before
     *                  the context was created
     * @param exception the failure
     * @since 2.0.0
     */
    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }
}
