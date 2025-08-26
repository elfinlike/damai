package com.bonss.system.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.bonss.common.core.domain.BaseEntity;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class BannerVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 轮播图ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long bannerId;

    /**
     * 轮播图片url
     */
    @NotNull(message = "轮播图图片url不能为空")
    private String imgUrl;


    /**
     * 轮播图片url
     */
    @NotNull(message = "轮播图图片名称不能为空")
    private String imgName;

    /**
     * 栏目排序优先级,值越大越靠前
     */
    private Integer sort;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;
}
