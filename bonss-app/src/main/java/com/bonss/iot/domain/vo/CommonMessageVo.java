package com.bonss.iot.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonMessageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 消息主类型（100-提示消息,200-告警信息,300-系统消息,400-家庭消息）
     */
    private String messageType;

    /**
     * 消息子类型（101-呼吸暂停事件....）
     */
    private String messageSubType;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息Icon
     */
    private String imgUrl;

    /**
     * 发送者类型（SYSTEM/USER/DEVICE）
     */
    private String senderType;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 接收者类型（USER/FAMILY/DEVICE）
     */
    private String receiverType;

    /**
     * 消息状态（0-未读/1-已读）
     */
    private Integer status;
    /**
     * 推送时间
     */
    private Date pushTime;

    /**
     * 阅读时间
     */
    private Date readTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
