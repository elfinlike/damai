package com.bonss.system.domain.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.bonss.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
public class ModuleVO {

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

    /** 创建者 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新者 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
