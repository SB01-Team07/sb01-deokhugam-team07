package com.part3.team07.sb01deokhugamteam07.exception.file;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class StorageInitException extends StorageException {

  public StorageInitException() {
    super(ErrorCode.STORAGE_INIT);
  }

  public static StorageInitException withPath(String path) {
    StorageInitException exception = new StorageInitException();
    exception.addDetail("path", path);
    return exception;
  }
}
