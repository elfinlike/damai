package com.bonss.iot.eventbus.listener;

import com.bonss.common.enums.MessageStatus;
import com.bonss.common.enums.SenderType;
import com.bonss.iot.domain.Message;
import com.bonss.iot.eventbus.event.FamilyMessageEvent;
import com.bonss.iot.mapper.MessageMapper;
import com.bonss.iot.util.JPushUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MessageListener extends AbstractEventListener {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private JPushUtil jPushUtil;

    /**
     * 处理家庭消息事件
     * @param event
     */
    @EventListener
    public void handleFamilyMessageEvent(FamilyMessageEvent event) {
        if (event.getUserIds() != null) {
            for (Long userId : event.getUserIds()) {
                // 1. 插入数据库
                insertMessage(event, userId);

                // 2. 极光推送
                if (event.isPublish()) {
                    pushMessage(userId, event);
                }
            }
            return;
        }

        // 1. 插入数据库
        insertMessage(event, event.getUserId());

        // 2. 极光推送
        if (event.isPublish()) {
            pushMessage(event.getUserId(), event);
        }
    }


    /**
     * 插入数据库
     * @param event
     * @param userId
     */
    private void insertMessage(FamilyMessageEvent event, Long userId) {
        Message message = new Message();
        message.setMessageType(event.getMessageType());
        message.setMessageSubType(event.getMessageSubType());
        message.setTitle(event.getTitle());
        message.setContent(event.getContent());
        message.setSenderType(SenderType.SENDER_TYPE_SYSTEM.getType());
        message.setReceiverId(userId);
        message.setReceiverType(SenderType.SENDER_TYPE_USER.getType());
        message.setStatus(MessageStatus.UNREAD.getCode());
        message.setDelFlag("0");
        message.setPushTime(new Date());

        messageMapper.insert(message);
    }

    /**
     * 极光推送
     * @param userId
     * @param event
     */
    private void pushMessage(Long userId, FamilyMessageEvent event) {
        try {
            Map<String, String> extras = new HashMap<>();
            extras.put("messageType", String.valueOf(event.getMessageType()));
            extras.put("messageSubType", String.valueOf(event.getMessageSubType()));

            // 调用极光推送工具类
            boolean result = jPushUtil.pushToUser(

                    userId.toString(),  // 使用用户ID作为极光别名
                    event.getTitle(),
                    event.getContent(),
                    extras
            );

            if (result) {
                log.info("成功向用户 {} 推送消息: {}", userId, event.getTitle());
            } else {
                log.error("向用户 {} 推送消息失败: {}", userId, event.getTitle());
            }
        } catch (Exception e) {
            log.error("极光推送过程中发生异常: {}", e.getMessage(), e);
        }
    }
}
