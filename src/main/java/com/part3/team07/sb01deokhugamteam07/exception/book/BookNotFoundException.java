package com.part3.team07.sb01deokhugamteam07.exception.book;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class BookNotFoundException extends BookException {

  public BookNotFoundException() {
    super(ErrorCode.BOOK_NOT_FOUND);
  }
}
