package com.part3.team07.sb01deokhugamteam07.exception.book;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;
import java.util.UUID;

public class BookNotFoundException extends BookException {

  public BookNotFoundException() {
    super(ErrorCode.BOOK_NOT_FOUND);
  }

  public static BookNotFoundException withId(UUID bookId) {
    BookNotFoundException exception = new BookNotFoundException();
    exception.addDetail("bookId", bookId);
    return exception;
  }
}
