package com.part3.team07.sb01deokhugamteam07.exception.notification;

import com.part3.team07.sb01deokhugamteam07.exception.DeokhugamException;
import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;

public class NotificationException extends DeokhugamException {
  public NotificationException(ErrorCode errorCode) {
    super(errorCode);
  }
}
