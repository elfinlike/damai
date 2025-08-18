package com.bonss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "consumable_type")
public class ConsumableType {
    /**
     * 产品型号耗材id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    /**
     * 型号id
     */
    private Long productId;
    /** 耗材名称 */
    private String name;


    /** 耗材大类（滤芯/电极片/滤网等） */
    private String type;

    /** 规格型号 */
    private String spec;
    /**
     * 默认提醒时长
     */
    private Integer defaultRemindDays;

    /**
     * 可供选择的时间选项
     */
    private String remindDayOptions;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String updateBy;


    /**是否删除，0--未删除，删除*/
    private String delFlag;
}
