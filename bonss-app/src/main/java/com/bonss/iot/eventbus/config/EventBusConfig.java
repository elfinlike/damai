package com.bonss.iot.eventbus.config;

import com.google.common.eventbus.AsyncEventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


import javax.annotation.Resource;

@Configuration
public class EventBusConfig {
    @Resource(name = "eventBusExecutor")
    private ThreadPoolTaskExecutor eventBusExecutor;

    /**
     * 定义事件总线bean
     */
    @Bean
    public AsyncEventBus eventBus() {
        return new AsyncEventBus(eventBusExecutor);
    }
}
