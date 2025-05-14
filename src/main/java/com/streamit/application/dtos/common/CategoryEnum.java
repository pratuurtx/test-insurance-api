package com.streamit.application.dtos.common;

import lombok.Getter;

@Getter
public enum CategoryEnum {
    BANNER("BANNER"),
    PROMOTION("PROMOTION"),
    INSURANCE("INSURANCE"),
    SUIT_INSURANCE("SUIT_INSURANCE");
    private final String value;

    CategoryEnum(String value) {
        if (!value.equals("BANNER")
                && !value.equals("PROMOTION")
                && !value.equals("INSURANCE")
                && !value.equals("SUIT_INSURANCE")) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public static CategoryEnum fromValue(String value) {
        for (CategoryEnum type : CategoryEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + value);
    }
}
