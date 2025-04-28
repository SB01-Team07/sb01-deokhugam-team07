package com.part3.team07.sb01deokhugamteam07.exception.storage;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class StorageAlreadyExistsException extends StorageException {

  public StorageAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_STORAGE);
  }

  public static StorageAlreadyExistsException withFileName(String fileName) {
    StorageAlreadyExistsException exception = new StorageAlreadyExistsException();
    exception.addDetail("fileName", fileName);
    return exception;
  }
}
