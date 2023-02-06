package org.newcih.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PremainDemo {
    public static final Logger LOGGER = LoggerFactory.getLogger(PremainDemo.class);

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            Thread.sleep(10_000);
            LOGGER.info(new PremainDemo().getName());
        }
    }

    public String getName() {
        System.out.println(1);
        return "first version getName method !";
    }
}
