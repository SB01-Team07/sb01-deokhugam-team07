package com.part3.team07.sb01deokhugamteam07.exception.review;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class InvalidReviewOrderException extends ReviewException{
    public InvalidReviewOrderException(String value) {
        super(ErrorCode.INVALID_REVIEW_REQUEST);
        this.addDetail("invalidValue", value);
    }
}
