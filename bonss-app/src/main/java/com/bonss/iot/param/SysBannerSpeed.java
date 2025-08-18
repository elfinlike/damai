package com.bonss.iot.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysBannerSpeed {
    /**
     * 轮播图切换速率
     */
    private Integer speed;

    /**
     * 动画过度速率
     */
    private Integer duration;
}
