package com.part3.team07.sb01deokhugamteam07.type;

import java.util.Arrays;

public enum ReviewOrderBy {
    CREATED_AT("createdAt"),
    RATING("rating");

    private final String value;

    ReviewOrderBy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ReviewOrderBy from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid orderBy: " + value));
    }
}