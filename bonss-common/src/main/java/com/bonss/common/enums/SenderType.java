package com.bonss.common.enums;

public enum SenderType {
    SENDER_TYPE_USER("USER"),
    SENDER_TYPE_SYSTEM("SYSTEM"),
    SENDER_TYPE_DEVICE("DEVICE");

    private final String type;

    SenderType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static SenderType fromType(String type) {
        for (SenderType senderType : values()) {
            if (senderType.type.equals(type)) {
                return senderType;
            }
        }
        throw new IllegalArgumentException("未知的SenderType: " + type);
    }
}