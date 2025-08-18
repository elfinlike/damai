package com.bonss.iot.eventbus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "eventbus.threadpool")
@Data
public class EventBusExecutorProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private int keepAliveSeconds;
    private int queueCapacity;
    private String threadNamePrefix;
    private boolean waitForTasksToCompleteOnShutdown;
    private int awaitTerminationSeconds;
}
