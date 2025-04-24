package com.part3.team07.sb01deokhugamteam07.exception.comment;

import com.part3.team07.sb01deokhugamteam07.exception.DeokhugamException;
import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class CommentException extends DeokhugamException {

  public CommentException(ErrorCode errorCode) {
    super(errorCode);
  }


}
