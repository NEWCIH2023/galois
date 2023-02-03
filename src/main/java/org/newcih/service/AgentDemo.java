package org.newcih.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentDemo {

    public static final Logger LOGGER = LoggerFactory.getLogger(AgentDemo.class);

    public static void main(String[] args) {

        while (true) {
            try {
                Thread.sleep(5000);
                AgentDemo agentDemo = new AgentDemo();
                agentDemo.test();
            } catch (Exception e) {
            }
        }

    }

    public String test() {
        LOGGER.info("invoke test method");
        return "test";
    }
}
