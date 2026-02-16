package com.springboot.usercrud.util;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    ADMIN,
    MANAGER,
    EMPLOYEE;

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
