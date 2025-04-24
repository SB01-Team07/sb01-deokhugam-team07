package com.part3.team07.sb01deokhugamteam07.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
  private final LocalDateTime timestamp;
  private final String code;
  private final String message;
  private final Map<String, Object> details;
  private final String exceptionType;
  private final int status;

  public ErrorResponse(DeokhugamException exception, int status) {
    this(LocalDateTime.now(),
        exception.getErrorCode().name(),
        exception.getMessage(),
        exception.getDetails(),
        exception.getClass().getSimpleName(),
        status);
  }

  public ErrorResponse(Exception exception, int status) {
    this(LocalDateTime.now(),
        resolveErrorCode(exception),
        exception.getMessage(),
        new HashMap<>(),
        exception.getClass().getSimpleName(),
        status);
  }

  private static String resolveErrorCode(Exception ex) {
    return switch (ex.getClass().getSimpleName()) {
      case "ConstraintViolationException" -> "VALIDATION_FAILED";
      case "IllegalArgumentException"      -> "INVALID_ARGUMENT";
      default                              -> "INTERNAL_ERROR";
    };
  }
}
