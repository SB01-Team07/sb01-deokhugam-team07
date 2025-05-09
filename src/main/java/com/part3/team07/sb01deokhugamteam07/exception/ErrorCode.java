package com.part3.team07.sb01deokhugamteam07.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {
  // 공통
  VALIDATION_FAILED("유효하지 않은 입력입니다."),

  // Book
  DUPLICATE_BOOK("이미 존재하는 도서입니다."),
  BOOK_NOT_FOUND("도서를 찾을 수 없습니다."),
  INVALID_SORT_FIELD("잘못된 정렬 필드입니다."),

  // Storage
  STORAGE_INIT("저장소 초기화 중 오류가 발생하였습니다."),
  DUPLICATE_STORAGE("이미 존재하는 리소스입니다."),
  STORAGE_SAVE_FAILED("리소스 저장 중 오류가 발생하였습니다."),

  // Ocr
  OCR_ISBN_NOT_FOUND("ISBN을 찾을 수 없습니다."),
  OCR_PROCESSING_FAILED("OCR 처리 중 오류가 발생하였습니다."),

  //Comment
  COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다"),
  COMMENT_UNAUTHORIZED("권한이 없습니다"),
  INVALID_COMMENT_CURSOR("잘못된 커서 포맷입니다."),
  INVALID_COMMENT_DIRECTION("정렬 방향은 ASC 또는 DESC만 가능합니다."),
  INVALID_COMMENT_SORT_BY("지원하지 않는 정렬 필드입니다."),

  // User
  NOT_FOUND_USER("존재하지 않는 유저입니다"),
  BAD_CREDENTIAL("잘못된 요청입니다 - 올바르지 않은 인증"),
  DUPLICATE_EMAIL("이메일이 중복되었습니다."),
  
  //Review 관련 에러 코드
  REVIEW_NOT_FOUND("리뷰 정보를 찾을 수 없습니다."),
  DUPLICATE_REVIEW("이미 작성한 리뷰가 존재합니다."),
  REVIEW_UNAUTHORIZED("리뷰에 대한 권한이 없습니다."),
  INVALID_REVIEW_REQUEST("리뷰 요청이 잘못되었습니다."), // (정렬 기준 오류, 페이지네이션 오류 등 포함)

  //Notification
  NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다."),
  NOTIFICATION_UNAUTHORIZED("알림 수정 권한이 없습니다.");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

}
