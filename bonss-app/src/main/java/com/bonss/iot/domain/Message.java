package com.bonss.iot.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息中心实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("message")
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    /**
     * 消息主类型（100-提示消息,200-告警信息,300-系统消息,400-家庭消息）
     */
    private Integer messageType;

    /**
     * 消息子类型（101-呼吸暂停事件....）
     */
    private Integer messageSubType;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

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
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
