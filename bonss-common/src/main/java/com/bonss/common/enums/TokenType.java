package com.bonss.common.enums;

public enum TokenType {
    SHORT_TOKEN(0,"短期token"),
    LONG_TOKEN(1,"长期token");


    private final int code;
    private final String info;

    TokenType(int code, String info) {
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
    public static String getInfoByCode(int code) {
        for (TokenType value : TokenType.values()) {
            if (value.code == code) {
                return value.info;
            }
        }
        return null;
    }
}
