package com.part3.team07.sb01deokhugamteam07.exception.review;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

import java.util.UUID;

public class ReviewAlreadyExistsException extends ReviewException {
    public ReviewAlreadyExistsException() {
        super(ErrorCode.DUPLICATE_REVIEW);
    }

    public static ReviewAlreadyExistsException withUserIdAndBookId(UUID userId, UUID bookId) {
        ReviewAlreadyExistsException exception = new ReviewAlreadyExistsException();
        exception.addDetail("userId", userId);
        exception.addDetail("bookId", bookId);
        return exception;
    }
}
