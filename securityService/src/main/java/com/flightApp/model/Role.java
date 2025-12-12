package com.flightApp.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
	ROLE_USER,
	ROLE_ADMIN;

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) return null;
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Role: " + value + ". Allowed: ROLE_USER, ROLE_ADMIN");
        }
    }
}