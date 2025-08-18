package com.bonss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bonss.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceConsumableType extends BaseEntity{
    /**
     * 产品型号耗材id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 型号id
     */
    private Long deviceId;
    /**
     * 耗材id
     */
    private Long productConsumableId;
    /**
     * 默认提醒时长
     */
    private Integer defaultRemindDays;

    /**
     * 可供选择的时间选项
     */
    private String remindDayOptions;

    /**
     * 绑定时间
     */
    private LocalDateTime bindTime;
}
