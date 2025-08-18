package com.bonss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("model_tutorial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelTutorial {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 说明书id
     */
    private Long tutorialId;

    /**
     * 型号id
     */
    private Long modelId;

    /**
     * 是否删除
     */
    private String delFlag;
}
