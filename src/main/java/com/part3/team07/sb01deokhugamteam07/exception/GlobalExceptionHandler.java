package com.part3.team07.sb01deokhugamteam07.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//  @ExceptionHandler(Exception.class)
//  public ResponseEntity<ErrorResponse> handleException(Exception e) {
//    log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
//    ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//    return ResponseEntity
//        .status(HttpStatus.INTERNAL_SERVER_ERROR)
//        .body(errorResponse);
//  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex) {
    log.error("요청 값 제약 조건 위반: {}", ex.getMessage(), ex);
    ErrorResponse errorResponse = new ErrorResponse(ex, HttpStatus.BAD_REQUEST.value());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

//  @ExceptionHandler(MethodArgumentNotValidException.class)
//  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
//      ConstraintViolationException ex) {
//    log.error("요청 유효성 검사 실패: {}", ex.getMessage());
//    ErrorResponse errorResponse = new ErrorResponse(ex, HttpStatus.BAD_REQUEST.value());
//
//    return ResponseEntity
//        .status(HttpStatus.BAD_REQUEST)
//        .body(errorResponse);
//  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      ConstraintViolationException ex) {
    log.error("잘못된 인자값 전달: {}", ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(ex, HttpStatus.BAD_REQUEST.value());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ErrorResponse> handleNullPointerException(
      ConstraintViolationException ex) {
    log.error("null 객체 참조: {}", ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(ex, HttpStatus.BAD_REQUEST.value());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(DeokhugamException.class)
  public ResponseEntity<ErrorResponse> handleDeokhugamException(DeokhugamException exception) {
    log.error("커스텀 예외 발생: code={}, message={}", exception.getErrorCode(), exception.getMessage(), exception);
    HttpStatus status = determineHttpStatus(exception);
    ErrorResponse response = new ErrorResponse(exception, status.value());

    return ResponseEntity
        .status(status)
        .body(response);
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
