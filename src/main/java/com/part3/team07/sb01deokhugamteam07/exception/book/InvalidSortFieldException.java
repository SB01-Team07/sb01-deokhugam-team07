package com.part3.team07.sb01deokhugamteam07.exception.book;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class InvalidSortFieldException extends BookException {

  public InvalidSortFieldException() {
    super(ErrorCode.INVALID_SORT_FIELD);
  }

  public static InvalidSortFieldException withField(String field) {
    InvalidSortFieldException exception = new InvalidSortFieldException();
    exception.addDetail("field", field);
    return exception;
  }
}
