package com.part3.team07.sb01deokhugamteam07.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // Book 관련 에러 코드
  DUPLICATE_ISBN("이미 존재하는 ISBN입니다.");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }
}
