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

  //Review 관련 에러 코드
  REVIEW_NOT_FOUND("리뷰 정보를 찾을 수 없습니다."),
  DUPLICATE_REVIEW_EXISTS("이미 작성한 리뷰가 존재합니다."),
  REVIEW_UNAUTHORIZED_DELETE("리뷰 삭제 권한이 없습니다."),
  REVIEW_UNAUTHORIZED_UPDATE("리뷰 수정 권한이 없습니다."),
  INVALID_REVIEW_REQUEST("리뷰 요청이 잘못되었습니다."); // (정렬 기준 오류, 페이지네이션 오류 등 포함)

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

}
