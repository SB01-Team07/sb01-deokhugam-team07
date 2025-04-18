package com.part3.team07.sb01deokhugamteam07.exception;

import com.part3.team07.sb01deokhugamteam07.dto.user.UserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserLoginRequest;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserRegisterRequest;
import com.part3.team07.sb01deokhugamteam07.exception.user.DuplicateUserEmailException;
import com.part3.team07.sb01deokhugamteam07.exception.user.IllegalUserPasswordException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateUserEmailException.class)
  public UserRegisterRequest duplicatedEmail(DuplicateUserEmailException e) {
    log.error("Duplicate Email: {}", e.getRequest().email());

    return e.getRequest();
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(IllegalUserPasswordException.class)
  public UserLoginRequest duplicatedEmail(IllegalUserPasswordException e) {
    log.error("Invalid Password: {}", e.getUserLoginRequest().password());

    return e.getUserLoginRequest();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public UserDto failValidateArgument(MethodArgumentNotValidException e) {
    e.getBindingResult().getFieldErrors().forEach(error -> {
      String field = error.getField();
      String failedValue = String.valueOf(error.getRejectedValue());
      log.error("Validation failed - field: {}, value: {}", field, failedValue);
    });

    UserRegisterRequest request = (UserRegisterRequest) e.getBindingResult().getTarget();

    return UserDto.builder()
        .nickname(request.nickname())
        .email(request.email())
        .createdAt(LocalDateTime.now())
        .build();
  }

//  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//  @ExceptionHandler(Exception.class)
//  public void internalException(Exception e) {
//  }
}
