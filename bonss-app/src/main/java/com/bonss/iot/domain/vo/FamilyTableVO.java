package com.bonss.iot.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bonss.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FamilyTableVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 家庭ID
     */
    @TableId(value = "id")
    private String id;

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
    private Long memberCount;

    /** 创建者 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新者 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 备注 */
    private String remark;
}
