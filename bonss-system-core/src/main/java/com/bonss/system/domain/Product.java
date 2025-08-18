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
public class Product{

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 产品编码（唯一） */
    private String productCode;

    /** 产品名称（唯一） */
    private String name;

    /** 所属IoT产品ID */
    private Long iotProductId;

    /** 所属栏目ID */
    private Long categoryId;

    /** 型号数量 */
    private Integer modelCount;

    /** 上架状态：0-下架，1-上架 */
    private Integer shelfStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String updateBy;

    /** 删除标记：0-未删除，1-删除 */
    private String delFlag;

    private String remark;

}
