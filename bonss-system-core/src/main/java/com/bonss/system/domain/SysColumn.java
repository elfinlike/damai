package com.bonss.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.bonss.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 系统栏目表 sys_banner
 *
 * @author bonss
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_column")
public class SysColumn extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 栏目ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long columnId;

    /**
     * 栏目名称
     */
    @NotNull(message = "栏目名称不能为空,长度为 100 ")
    private String columnName;

    /**
     * 栏目排序优先级
     */
    @TableField("column_sort")
    private Integer sort;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * icon访问url
     */
    private String iconUrl;
}
