package com.part3.team07.sb01deokhugamteam07.exception.user;

import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserRegisterRequest;
import com.part3.team07.sb01deokhugamteam07.exception.DeokhugamException;
import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class DuplicateUserEmailException extends DeokhugamException {

  private final UserRegisterRequest request;

  public DuplicateUserEmailException(UserRegisterRequest request) {
    super(ErrorCode.DUPLICATE_EMAIL);
    this.request = request;
  }

  public UserRegisterRequest getRequest() {
    return request;
  }
}
