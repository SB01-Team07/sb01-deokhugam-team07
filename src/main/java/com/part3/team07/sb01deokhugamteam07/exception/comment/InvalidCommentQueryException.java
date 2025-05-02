package com.part3.team07.sb01deokhugamteam07.exception.comment;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class InvalidCommentQueryException extends CommentException {

  public InvalidCommentQueryException(ErrorCode errorCode) {
    super(errorCode);
  }

  public static InvalidCommentQueryException sortBy() {
    return new InvalidCommentQueryException(ErrorCode.INVALID_COMMENT_SORT_BY);
  }

  public static InvalidCommentQueryException cursor() {
    return new InvalidCommentQueryException(ErrorCode.INVALID_COMMENT_CURSOR);
  }

  public static InvalidCommentQueryException direction() {
    return new InvalidCommentQueryException(ErrorCode.INVALID_COMMENT_DIRECTION);
  }
}
