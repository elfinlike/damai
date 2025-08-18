package com.bonss.iot.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageVo<T> implements Serializable {
    private static final long serialVersionUID = 42L;
    // 消息基本属性
    private String type;            // 消息类型
    private T payload;              // 消息内容（泛型）
    private long timestamp;         // 时间戳（毫秒）

}
