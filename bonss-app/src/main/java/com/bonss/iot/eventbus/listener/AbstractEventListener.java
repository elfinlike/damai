package com.bonss.iot.eventbus.listener;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * 将所有继承自该类的监听器自动注册到事件总线中
 */
@Slf4j
public class AbstractEventListener {

    @Autowired
    private EventBus eventBus;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        log.info("事件监听器 {} 已注册到事件总线", this.getClass().getName());
    }
}

