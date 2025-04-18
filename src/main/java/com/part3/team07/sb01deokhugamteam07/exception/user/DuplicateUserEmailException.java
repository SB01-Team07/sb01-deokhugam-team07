package com.part3.team07.sb01deokhugamteam07.exception.user;

import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserRegisterRequest;

public class DuplicateUserEmailException extends RuntimeException {

  private final UserRegisterRequest request;

  public DuplicateUserEmailException(UserRegisterRequest request) {
    this.request = request;
  }

  public UserRegisterRequest getRequest() {
    return request;
  }
}
