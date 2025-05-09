package com.part3.team07.sb01deokhugamteam07.exception.ocr;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class OcrNotFoundException extends OcrException {

  public OcrNotFoundException() {
    super(ErrorCode.OCR_ISBN_NOT_FOUND);
  }
}
