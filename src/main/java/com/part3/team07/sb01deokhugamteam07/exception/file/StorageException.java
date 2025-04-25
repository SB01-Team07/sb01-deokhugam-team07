package com.part3.team07.sb01deokhugamteam07.exception.file;

import com.part3.team07.sb01deokhugamteam07.exception.DeokhugamException;
import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class StorageException extends DeokhugamException {

  public StorageException(ErrorCode errorCode) {
    super(errorCode);
  }

  public StorageException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
