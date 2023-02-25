package org.newcih.galoisdemo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class InfoCollectService implements ApplicationListener<ContextRefreshedEvent> {

    public static final Logger LOGGER = LoggerFactory.getLogger(InfoCollectService.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("当前上下文对象 {}", event.getApplicationContext());
    }
}
