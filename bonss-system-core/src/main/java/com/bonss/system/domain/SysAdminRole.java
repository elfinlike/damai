package com.bonss.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 管理员和角色关联 sys_user_role
 *
 * @author ct
 */
public class SysAdminRole {
    /**
     * 用户ID
     */
    private Long adminId;

    /**
     * 角色ID
     */
    private Long roleId;

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("adminId", getAdminId())
                .append("roleId", getRoleId())
                .toString();
    }
}
