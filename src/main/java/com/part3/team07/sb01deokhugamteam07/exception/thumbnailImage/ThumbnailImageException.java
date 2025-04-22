package com.part3.team07.sb01deokhugamteam07.exception.thumbnailImage;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class ThumbnailImageException extends RuntimeException {

  public ThumbnailImageException(ErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}
