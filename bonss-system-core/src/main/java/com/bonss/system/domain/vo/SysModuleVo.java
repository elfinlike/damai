package com.bonss.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 首页活动Vo
 *
 * @author bonss
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysModuleVo {
    private static final long serialVersionUID = 1L;

    /**
     * 模块ID
     */
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

}
