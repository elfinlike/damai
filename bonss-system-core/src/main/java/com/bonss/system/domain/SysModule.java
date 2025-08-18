package com.bonss.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.bonss.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 首页活动表 sys_module
 *
 * @author bonss
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_module")
public class SysModule extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 模块ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long moduleId;

    /**
     * 模块标题图片url
     */
    @NotNull(message = "模块标题图片url不能为空")
    private String url;

    /**
     * 轮播图片url
     */
    @NotNull(message = "模块名称不能为空")
    private String moduleName;

    /**
     * 排序优先级,值越大越靠前
     */
    private Integer sort;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

    @NotNull(message = "模块所属栏目不能为空")
    @TableField(exist = false)
    private Long[] columnIds;

}
