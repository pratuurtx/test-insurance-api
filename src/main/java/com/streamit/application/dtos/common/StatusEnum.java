package com.streamit.application.dtos.common;

import lombok.Getter;

@Getter
public enum StatusEnum {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String value;

    StatusEnum(String value) {
        if (!value.equals("ACTIVE") && !value.equals("INACTIVE")) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public static StatusEnum fromValue(String value) {
        for (StatusEnum status : StatusEnum.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }

}
