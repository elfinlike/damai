package com.bonss.iot.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统添加栏目统一接收参数对象
 * @author bonss
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysBannerParam {
    private static final long serialVersionUID = 1L;
    /**
     * 轮播图id
     */
    private Long bannerId;

    /**
     * 跳转直播ID
     */
    private Long liveId;

    /**
     * 轮播图状态
     */
    private String status;

    /**
     * 删除标志 0表示正常 2表示已经删除
     */
    private String delFlag;

    /**
     * 排序字段  值越大，越靠前
     */
    private Integer sort;


    /**
     * 轮播图片名称
     */
    private String imgName;

    /**
     * 轮播图片类型 0代表直播小程序 1代表电子彩页
     */
    private String type;

    /**
     * 跳转书籍Id
     */
    private Long bookId;
}
