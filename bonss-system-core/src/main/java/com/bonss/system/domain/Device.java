package com.bonss.system.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "device")
public class Device {

    @TableId(type = IdType.AUTO)
    private Long id;                // 设备唯一ID

    private String name;            // 设备名称

    private String deviceCode;      // 设备编码（唯一标识）

    private Long modelId;           // 型号ID

    private Long userId;            // 用户ID

    private Date createTime;        // 创建时间

    private String createBy;        // 创建者

    private LocalDateTime updateTime;        // 更新时间

    private String updateBy;        // 更新者

    private LocalDateTime bindTime; // 首次激活时间

    private LocalDateTime lastOnlineTime;    // 最近在线时间

    private LocalDateTime lastOfflineTime;   // 最近离线时间

    private LocalDateTime lastRunTime;       // 最近运行时间

    private LocalDateTime lastStopTime;      // 最近停止时间

    private String ipAddress;       // MAC地址

    private Integer onlineStatus;   // 在线状态: 0-离线, 1-在线

    private Integer runStatus;      // 运行状态: 0-停止, 1-运行, 2-故障, 3-特例

    private Long otherInfoId;       // 设备其他信息ID

    private String remark;          // 备注信息

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
