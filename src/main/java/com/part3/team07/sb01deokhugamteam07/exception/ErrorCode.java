package com.part3.team07.sb01deokhugamteam07.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {
  // Book 관련 에러 코드
  DUPLICATE_BOOK("이미 존재하는 도서입니다."),
  BOOK_NOT_FOUND("도서를 찾을 수 없습니다."),

  // ThumbnailImage 관련 에러 코드
  THUMBNAIL_IMAGE_ALREADY_EXISTS("이미 존재하는 썸네일 이미지입니다."),
  THUMBNAIL_IMAGE_NOT_FOUND("해당 썸네일 이미지가 존재하지 않습니다."),
  THUMBNAIL_IMAGE_STORAGE_INIT("썸네일 저장소 초기화 중 오류가 발생하였습니다."),
  THUMBNAIL_IMAGE_STORAGE("썸네일 이미지 저장 중 오류가 발생하였습니다."),

  //Comment
  COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다"),
  COMMENT_UNAUTHORIZED("권한이 없습니다"),

  // User
  NOT_FOUND_USER("존재하지 않는 유저입니다"),
  BAD_CREDENTIAL("잘못된 요청입니다 - 올바르지 않은 인증"),
  DUPLICATE_EMAIL("이메일이 중복되었습니다.");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

}
