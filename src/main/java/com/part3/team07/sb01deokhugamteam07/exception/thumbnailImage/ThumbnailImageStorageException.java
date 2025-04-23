package com.part3.team07.sb01deokhugamteam07.exception.thumbnailImage;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class ThumbnailImageStorageException extends ThumbnailImageException {

  public ThumbnailImageStorageException() {
    super(ErrorCode.THUMBNAIL_IMAGE_STORAGE);
  }
}
