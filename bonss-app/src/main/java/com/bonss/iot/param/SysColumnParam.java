package com.bonss.iot.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 系统添加栏目统一接收参数对象
 *
 * @author bonss
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysColumnParam {
    private static final long serialVersionUID = 1L;
    /**
     * 栏目唯一id
     */
    @NotBlank(message = "栏目id不能为空")
    private Long columnId;

    /**
     * 父栏目id
     */
    private Long parentId;

    /**
     * 栏目名称
     */
    private String columnName;

    /**
     * 栏目排序优先级
     */
    private Integer sort;

    /**
     *  备注信息
     */
    private String remark;

    /**
     * 产品线形象图
     */
    private String productImgUrl;

    /**
     * 是否为云视频栏
     */
    private String videoFlag;

}
