package com.bonss.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 设备耗材详情
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceConsumableVO {
    private String name;
    private String type;
    private String spec;
    private Integer defaultRemindDays;
    private Integer remainDays;
}
