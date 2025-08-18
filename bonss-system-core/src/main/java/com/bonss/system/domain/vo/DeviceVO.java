package com.bonss.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceVO {
    /** id */
    private Long id;
    /** 设备名称 */
    private String name;
    /** 所属产品 */
    private String productName;
    /** 型号 */
    private String modelName;
    /** 在线状态 */
    private Integer onlineStatus;
    /** 最近在线状态 */
    private LocalDateTime lastOnlineTime;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** MAC地址 */
    private String ipAddress;
}

