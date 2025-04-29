package com.part3.team07.sb01deokhugamteam07.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 필수 파라미터 누락
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
    log.warn("필수 요청 파라미터 누락: {}", ex.getMessage());
    ErrorResponse response = new ErrorResponse(ex, HttpStatus.BAD_REQUEST.value());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // 필수 헤더 누락
  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException ex) {
    log.warn("필수 요청 헤더 누락: {}", ex.getMessage());
    ErrorResponse response = new ErrorResponse(ex, HttpStatus.BAD_REQUEST.value());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // 타입 변환 실패 (UUID, int, datetime 변환 실패)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    log.warn("요청 값 타입 불일치: {}", ex.getMessage());
    ErrorResponse response = new ErrorResponse(ex, HttpStatus.BAD_REQUEST.value());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }


  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex) {
    log.error("요청 값 제약 조건 위반: {}", ex.getMessage(), ex);
    ErrorResponse errorResponse = new ErrorResponse(ex, HttpStatus.BAD_REQUEST.value());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      ConstraintViolationException ex) {
    log.error("요청 유효성 검사 실패: {}", ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(ex, HttpStatus.BAD_REQUEST.value());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

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
      case BOOK_NOT_FOUND, REVIEW_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case DUPLICATE_BOOK, DUPLICATE_REVIEW -> HttpStatus.CONFLICT;
      case REVIEW_UNAUTHORIZED -> HttpStatus.FORBIDDEN;
      case INVALID_REVIEW_REQUEST -> HttpStatus.BAD_REQUEST;
      // 에러 코드 추가 시 업데이트
//      case  -> HttpStatus.UNAUTHORIZED;
//      case  -> HttpStatus.INTERNAL_SERVER_ERROR;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }

  @Order(Ordered.LOWEST_PRECEDENCE)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
    ErrorResponse errorResponse = new ErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR.value());

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }

}
