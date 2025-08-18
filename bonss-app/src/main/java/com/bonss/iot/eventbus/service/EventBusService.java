package com.bonss.iot.eventbus.service;

import com.bonss.iot.eventbus.event.FamilyMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EventBusService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void sendFamilyMessageEvent(FamilyMessageEvent familyMessageEvent) {
        eventPublisher.publishEvent(familyMessageEvent);
    }


}
