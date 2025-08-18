package com.bonss.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.bonss.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 系统栏目表 sys_column
 *
 * @author bonss
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_banner")
public class SysBanner extends BaseEntity {
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
     * 轮播图跳转直播ID
     */
    private Long liveId;
    /**
     * 轮播图跳转书籍ID
     */

    private Long bookId;

    /**
     * 栏目排序优先级,值越大越靠前
     */
    private Integer sort;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

    @TableField(exist = false)
    private String liveTitle;

    /**
     * 轮播图类型 0代表直播小程序,1代表电子彩页
     */
    private String type;

    @TableField(exist = false)
    private String bookName;

}
