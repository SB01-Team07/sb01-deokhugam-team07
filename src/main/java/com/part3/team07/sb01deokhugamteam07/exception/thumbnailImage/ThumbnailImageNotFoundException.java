package com.part3.team07.sb01deokhugamteam07.exception.thumbnailImage;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class ThumbnailImageNotFoundException extends ThumbnailImageException {

  public ThumbnailImageNotFoundException() {
    super(ErrorCode.THUMBNAIL_IMAGE_NOT_FOUND);
  }
}
