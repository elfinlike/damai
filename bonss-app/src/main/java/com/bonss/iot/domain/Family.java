package com.bonss.iot.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.List;

import com.bonss.common.core.domain.BaseEntity;
import com.bonss.common.core.domain.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 家庭表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("family")
public class Family extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 家庭ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 家庭名称
     */
    private String name;

    /**
     * 创建者用户ID（默认管理员）
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 家庭二维码（Base64字符串或链接）
     */
    @TableField("qr_code")
    private String qrCode;

    /**
     * 家庭成员数量
     */
    @TableField(exist = false)
    private Integer memberCount;
}
