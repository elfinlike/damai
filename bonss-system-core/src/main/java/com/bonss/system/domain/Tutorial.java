package com.bonss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tutorial {

    /**
     * 图文教程的id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 对应的产品id
     */
    private Integer productId;

    /**
     * 图文教程的标题
     */
    private String title;

    /**
     * 图文教程的内容
     */
    private String content;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 修改者
     */
    private String updateBy;

    /**
     * 是否删除，0--未删除，2--删除
     */

    private String delFlag;
}
