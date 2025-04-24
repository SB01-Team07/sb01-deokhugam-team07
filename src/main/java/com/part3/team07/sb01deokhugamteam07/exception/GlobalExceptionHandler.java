package com.part3.team07.sb01deokhugamteam07.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DeokhugamException.class)
  public ResponseEntity<ErrorResponse> handleDeokhugamException(DeokhugamException exception) {
    log.error("커스텀 예외 발생: code={}, message={}", exception.getErrorCode(), exception.getMessage(), exception);
    HttpStatus status = determineHttpStatus(exception);
    ErrorResponse response = new ErrorResponse(exception, status.value());
    return ResponseEntity
        .status(status)
        .body(response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Void> handleConstraintViolationException(
      ConstraintViolationException ex) {

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  private HttpStatus determineHttpStatus(DeokhugamException exception) {
    ErrorCode errorCode = exception.getErrorCode();
    return switch (errorCode) {
      case BOOK_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case DUPLICATE_BOOK -> HttpStatus.CONFLICT;
      // 에러 코드 추가 시 업데이트
//      case  -> HttpStatus.UNAUTHORIZED;
//      case  -> HttpStatus.BAD_REQUEST;
//      case  -> HttpStatus.INTERNAL_SERVER_ERROR;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }

}
