package com.example.fbs.fbs.model.entity;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public enum Role {
    ADMIN,
    CLIENT;

    public static Role from(String name) {
        if (StringUtils.equalsIgnoreCase(ADMIN.name(), name)) {
            return ADMIN;
        }
        return CLIENT;
    }

    @Override
    public String toString() {
        return name();
    }
}
