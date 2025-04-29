package com.part3.team07.sb01deokhugamteam07.exception;

import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserLoginRequest;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserRegisterRequest;
import com.part3.team07.sb01deokhugamteam07.exception.user.DuplicateUserEmailException;
import com.part3.team07.sb01deokhugamteam07.exception.user.IllegalUserPasswordException;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateUserEmailException.class)
  public UserRegisterRequest duplicatedEmail(DuplicateUserEmailException e) {
    log.error("Duplicate Email: {}", e.getRequest().email());

    return e.getRequest();
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(UserNotFoundException.class)
  public UUID userNotFound(UserNotFoundException e) {
    log.error("User Not Found: {}", e.getUserId());

    return e.getUserId();
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(IllegalUserPasswordException.class)
  public UserLoginRequest duplicatedEmail(IllegalUserPasswordException e) {
    log.error("Invalid Password: {}", e.getUserLoginRequest().password());

    return e.getUserLoginRequest();
  }
}
