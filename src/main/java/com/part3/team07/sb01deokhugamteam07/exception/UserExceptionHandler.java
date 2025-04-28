package com.part3.team07.sb01deokhugamteam07.exception;

import com.part3.team07.sb01deokhugamteam07.exception.user.DuplicateUserEmailException;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateUserEmailException.class)
  public ErrorResponse duplicatedEmail(DuplicateUserEmailException e) {
    log.error("Duplicate Email: {}", e.getRequest().email());

    return new ErrorResponse(
        e,
        HttpStatus.CONTINUE.value()
    );
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(UserNotFoundException.class)
  public ErrorResponse userNotFound(UserNotFoundException e) {
    log.error("User Not Found");

    return new ErrorResponse(
        e,
        HttpStatus.NOT_FOUND.value()
    );
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(BadCredentialsException.class)
  public ErrorResponse badCredential(BadCredentialsException e) {
    log.error("Bad Credential: {}", e.getMessage());

    return new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.FORBIDDEN.toString(),
        ErrorCode.BAD_CREDENTIAL.getMessage(),
        Map.of(),
        BadCredentialsException.class.getTypeName(),
        HttpStatus.FORBIDDEN.value()
    );
  }
}
