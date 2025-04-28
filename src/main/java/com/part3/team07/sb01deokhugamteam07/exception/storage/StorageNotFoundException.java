package com.part3.team07.sb01deokhugamteam07.exception.storage;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class StorageNotFoundException extends StorageException {

  public StorageNotFoundException() {
    super(ErrorCode.STORAGE_NOT_FOUND);
  }

  public static StorageNotFoundException withFileName(String fileName) {
    StorageNotFoundException exception = new StorageNotFoundException();
    exception.addDetail("fileName", fileName);
    return exception;
  }
}
