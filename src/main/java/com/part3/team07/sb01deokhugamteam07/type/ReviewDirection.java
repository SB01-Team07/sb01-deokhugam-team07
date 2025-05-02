package com.part3.team07.sb01deokhugamteam07.type;

public enum ReviewDirection {
    ASC, DESC;

    public static ReviewDirection from(String value) {
        try {
            return ReviewDirection.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid direction: " + value);
        }
    }
}