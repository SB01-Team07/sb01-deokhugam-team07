package com.part3.team07.sb01deokhugamteam07.exception.ocr;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class OcrProcessingFailedException extends OcrException {

  public OcrProcessingFailedException() {
    super(ErrorCode.OCR_PROCESSING_FAILED);
  }
}
