package com.part3.team07.sb01deokhugamteam07.exception.ocr;

import com.part3.team07.sb01deokhugamteam07.exception.DeokhugamException;
import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class OcrException extends DeokhugamException {

  public OcrException(ErrorCode errorCode) {
    super(errorCode);
  }
}
