package com.bonss.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.bonss.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("product_model")
public class ProductModel  {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 型号名称 */
    private String name;

    /** 所属产品ID */
    private Long productId;


    /** 型号编码*/
    private String modelCode;

    /** 型号图片 */
    private String modelImg;

    /** 状态：0=禁用，1=启用 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String updateBy;

    /** 删除标志：0=正常，2=删除 */
    private String delFlag;

    private String remark;
}
