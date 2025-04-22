package com.part3.team07.sb01deokhugamteam07.exception.user;

import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserLoginRequest;

public class UserNotFoundException extends RuntimeException {

  private final UserLoginRequest userLoginRequest;

  public UserNotFoundException(UserLoginRequest userLoginRequest) {
    this.userLoginRequest = userLoginRequest;
  }

  public UserLoginRequest getUserLoginRequest() {
    return userLoginRequest;
  }
}
