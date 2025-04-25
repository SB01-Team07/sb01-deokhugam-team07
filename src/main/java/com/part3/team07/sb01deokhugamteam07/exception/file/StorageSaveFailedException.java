package com.part3.team07.sb01deokhugamteam07.exception.file;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class StorageSaveFailedException extends StorageException {

  public StorageSaveFailedException() {
    super(ErrorCode.STORAGE_SAVE_FAILED);
  }

  public static StorageSaveFailedException withFileName(String fileName) {
    StorageSaveFailedException exception = new StorageSaveFailedException();
    exception.addDetail("fileName", fileName);
    return exception;
  }
}
