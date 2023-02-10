package org.newcih.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PremainDemo {
    public static final Logger LOGGER = LoggerFactory.getLogger(PremainDemo.class);

    public static void main(String[] args) throws InterruptedException {
        Thread.currentThread().join();
    }

    public String getName() {
        return " first versbon getName method !";
    }
}
