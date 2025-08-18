package com.bonss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bonss.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "model_consumable_type")
public class ModelConsumableType{
    /**
     * 产品型号耗材id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 型号id
     */
    private Long modelId;
    /**
     * 耗材id
     */
    private Long consumableTypeId;

    /** 删除标志：0=正常，2=删除 */
    private String delFlag;
}
