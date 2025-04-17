package com.part3.team07.sb01deokhugamteam07.exception.user;

public class DuplicateUserEmailException extends RuntimeException {

  public DuplicateUserEmailException() {
    super();
  }

  public DuplicateUserEmailException(String message) {
    super(message);
  }
}
