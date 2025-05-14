package com.streamit.application.dtos.common;

import lombok.Getter;

@Getter
public enum TypeEnum {
    BANNER("BANNER"),
    PROMOTION("PROMOTION"),
    INSURANCE("INSURANCE"),
    SUIT_INSURANCE("SUIT_INSURANCE");
    private final String value;

    TypeEnum(String value) {
        if (!value.equals("BANNER")
                && !value.equals("PROMOTION")
                && !value.equals("INSURANCE")
                && !value.equals("SUIT_INSURANCE")) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public static TypeEnum fromValue(String value) {
        for (TypeEnum type : TypeEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + value);
    }
}
