package com.part3.team07.sb01deokhugamteam07.exception.review;

import com.part3.team07.sb01deokhugamteam07.exception.DeokhugamException;
import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class ReviewException extends DeokhugamException {
    public ReviewException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ReviewException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
