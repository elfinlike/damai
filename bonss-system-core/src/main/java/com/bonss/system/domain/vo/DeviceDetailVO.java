package com.bonss.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDetailVO {

    //设备ID
    private Long deviceId;
    //设备名称
    private String deviceName;
    //产品ID
    private Long productId;
    //产品名称
    private String productName;
    //产品编码
    private String productCode;
    //设备型号名称
    private String modelName;
    //设备型号编码
    private String modelCode;
    //用户ID
    private Long userId;
    //用户名称
    private String userName;
    //设备在线状态
    private Integer onlineStatus;
    //最后一次上线时间
    private LocalDateTime lastOnlineTime;
    //绑定时间
    private LocalDateTime bindTime;
    //设备IP
    private String ipAddress;
}
