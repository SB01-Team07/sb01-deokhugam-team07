package com.part3.team07.sb01deokhugamteam07.exception.notification;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;
import java.util.UUID;

public class NotificationNotFoundException extends NotificationException {

  public NotificationNotFoundException(UUID notificationId) {
    super(ErrorCode.NOTIFICATION_NOT_FOUND);
    addDetail("notificationId", notificationId);
  }
}