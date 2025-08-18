package com.bonss.iot.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;

import com.bonss.common.core.domain.BaseEntity;
import com.bonss.common.core.domain.entity.SysUser;
import lombok.Data;

/**
 * 家庭成员表
 */
@Data
@TableName("family_member")
public class FamilyMember extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 家庭ID
     */
    @TableField("family_id")
    private Long familyId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 成员角色（0=管理员，1=普通成员）
     */
    private Integer role;

    /**
     * 加入状态（0=待审批，1=已加入，2=已拒绝）
     */
    private Integer status;

    /**
     * 申请加入时间
     */
    @TableField("request_time")
    private Date requestTime;

    /**
     * 管理员批准加入的时间
     */
    @TableField("approved_time")
    private Date approvedTime;

    /**
     * 关联用户
     */
    @TableField(exist = false)
    private SysUser user;
}
