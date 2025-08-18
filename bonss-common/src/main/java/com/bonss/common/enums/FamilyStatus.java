package com.bonss.common.enums;

public enum FamilyStatus {

    PENDING(0, "待审批"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝");

    private final int code;
    private final String description;

    FamilyStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据 code 获取对应的枚举
     */
    public static FamilyStatus fromCode(int code) {
        for (FamilyStatus status : FamilyStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的家庭状态 code: " + code);
    }
}
