package com.part3.team07.sb01deokhugamteam07.exception.review;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

import java.util.UUID;

public class ReviewNotFoundException extends ReviewException {
    public ReviewNotFoundException() {
        super(ErrorCode.REVIEW_NOT_FOUND);
    }

    public static ReviewNotFoundException withId(UUID reviewId) {
        ReviewNotFoundException exception = new ReviewNotFoundException();
        exception.addDetail("reviewId", reviewId);
        return exception;
    }
}
