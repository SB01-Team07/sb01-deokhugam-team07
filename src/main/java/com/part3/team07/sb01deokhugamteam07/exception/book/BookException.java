package com.part3.team07.sb01deokhugamteam07.exception.book;

import com.part3.team07.sb01deokhugamteam07.exception.DeokhugamException;
import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class BookException extends DeokhugamException {

  public BookException(ErrorCode errorCode) {
    super(errorCode);
  }

  public BookException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
