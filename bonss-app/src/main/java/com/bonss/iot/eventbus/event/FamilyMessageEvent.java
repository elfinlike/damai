package com.bonss.iot.eventbus.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMessageEvent {
    private Long familyId;
    private String familyName;
    private int messageType;
    private int messageSubType;
    private String title;
    private String content;
    private Long userId;//只发单个人
    private List<Long> userIds;//群发，否则填null
    private boolean isPublish;//是否推送
}
