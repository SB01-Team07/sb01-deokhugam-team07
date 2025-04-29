package com.part3.team07.sb01deokhugamteam07.exception.user;

import com.part3.team07.sb01deokhugamteam07.exception.DeokhugamException;
import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;
import lombok.Getter;

@Getter
public class UserNotFoundException extends DeokhugamException {

  public UserNotFoundException() {
    super(ErrorCode.NOT_FOUND_USER);
  }
}
