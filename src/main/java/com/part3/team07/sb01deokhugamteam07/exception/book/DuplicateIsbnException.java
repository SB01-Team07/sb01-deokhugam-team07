package com.part3.team07.sb01deokhugamteam07.exception.book;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class DuplicateIsbnException extends BookException {

  public DuplicateIsbnException() {
    super(ErrorCode.DUPLICATE_ISBN);
  }
}
