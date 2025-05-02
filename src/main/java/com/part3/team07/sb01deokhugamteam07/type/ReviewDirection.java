package com.part3.team07.sb01deokhugamteam07.type;

import com.part3.team07.sb01deokhugamteam07.exception.review.InvalidReviewOrderException;

public enum ReviewDirection {
    ASC, DESC;

    public static ReviewDirection from(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new InvalidReviewOrderException(value);
        }
    }
}