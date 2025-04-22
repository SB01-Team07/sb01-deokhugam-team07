package com.part3.team07.sb01deokhugamteam07.exception.thumbnailImage;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class ThumbnailImageAlreadyExistsException extends ThumbnailImageException {

  public ThumbnailImageAlreadyExistsException() {
    super(ErrorCode.THUMBNAIL_IMAGE_ALREADY_EXISTS);
  }
}
