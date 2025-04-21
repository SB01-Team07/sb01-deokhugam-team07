package com.part3.team07.sb01deokhugamteam07.exception.comment;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;
import java.util.UUID;

public class CommentNotFoundException extends CommentException {
  public CommentNotFoundException() {super(ErrorCode.COMMENT_NOT_FOUND);}

  public static CommentNotFoundException withId(UUID commentId){
    CommentNotFoundException exception = new CommentNotFoundException();
    exception.addDetail("commentId", commentId);
    return exception;
  }
}
