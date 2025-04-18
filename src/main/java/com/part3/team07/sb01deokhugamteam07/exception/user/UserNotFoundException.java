package com.part3.team07.sb01deokhugamteam07.exception.user;

import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserLoginRequest;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

  private UserLoginRequest userLoginRequest;
  private UUID userId;

  public UserNotFoundException(UserLoginRequest userLoginRequest) {
    this.userLoginRequest = userLoginRequest;
  }

  public UserNotFoundException(UUID userId) {
    this.userId = userId;
  }

}
