package com.part3.team07.sb01deokhugamteam07.exception.book;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class BookAlreadyExistsException extends BookException {

  public BookAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_BOOK);
  }

  public static BookAlreadyExistsException withIsbn(String isbn) {
    BookAlreadyExistsException exception = new BookAlreadyExistsException();
    exception.addDetail("isbn", isbn);
    return exception;
  }

}
