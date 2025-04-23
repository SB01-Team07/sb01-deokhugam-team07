package com.part3.team07.sb01deokhugamteam07.exception.comment;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;
import java.util.UUID;

public class CommentUnauthorizedException extends CommentException {

  public CommentUnauthorizedException() {
    super(ErrorCode.COMMENT_UNAUTHORIZED);
  }

}
