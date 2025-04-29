package com.part3.team07.sb01deokhugamteam07.exception.review;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

import java.util.UUID;

public class ReviewUnauthorizedException extends ReviewException {
    public ReviewUnauthorizedException() {
        super(ErrorCode.REVIEW_UNAUTHORIZED);
    }

    public static ReviewUnauthorizedException forDelete(UUID userId, UUID reviewId) {
        ReviewUnauthorizedException exception = new ReviewUnauthorizedException();
        exception.addDetail("action", "delete");
        exception.addDetail("userId", userId);
        exception.addDetail("reviewId", reviewId);
        return exception;
    }

    public static ReviewUnauthorizedException forUpdate(UUID userId, UUID reviewId) {
        ReviewUnauthorizedException exception = new ReviewUnauthorizedException();
        exception.addDetail("action", "update");
        exception.addDetail("userId", userId);
        exception.addDetail("reviewId", reviewId);
        return exception;
    }
}
