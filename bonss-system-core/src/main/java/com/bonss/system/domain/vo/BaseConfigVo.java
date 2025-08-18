package com.bonss.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 轮播图切换速率
     */
    private Integer speed;

    /**
     * 动画过度速率
     */
    private Integer duration;

    /**
     * 是否初始化
     */
    private Boolean init;

}
