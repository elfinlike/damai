package com.bonss.common.enums;

public enum FamilyRole {
    ADMIN(0,"管理员"),
    COMMON(1,"普通用户");


    private final int code;
    private final String info;

    FamilyRole(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    // 通过code获取info
    public static String getByCode(int code) {
        for (FamilyRole value : FamilyRole.values()) {
            if (value.code == code) {
                return value.info;
            }
        }
        return null;
    }
}
