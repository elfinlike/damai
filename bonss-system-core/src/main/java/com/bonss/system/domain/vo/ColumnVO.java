package com.bonss.system.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.bonss.common.core.domain.BaseEntity;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ColumnVO extends BaseEntity {
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
