package com.part3.team07.sb01deokhugamteam07.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class DeokhugamException extends RuntimeException {
  private final LocalDateTime timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  public DeokhugamException(ErrorCode errorCode) {
    super(errorCode.getMessage(), null, false, false);
    this.timestamp = LocalDateTime.now();
    this.errorCode = errorCode;
    this.details = new HashMap<>();
  }

  public DeokhugamException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.timestamp = LocalDateTime.now();
    this.errorCode = errorCode;
    this.details = new HashMap<>();
  }

  public void addDetail(String key, Object value) {
    this.details.put(key, value);
  }
}