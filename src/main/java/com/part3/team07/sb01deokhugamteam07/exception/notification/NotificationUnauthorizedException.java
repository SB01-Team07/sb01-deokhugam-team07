package com.part3.team07.sb01deokhugamteam07.exception.notification;

import com.part3.team07.sb01deokhugamteam07.exception.ErrorCode;
import java.util.UUID;

public class NotificationUnauthorizedException extends NotificationException {

  public NotificationUnauthorizedException(UUID userId) {
    super(ErrorCode.NOTIFICATION_UNAUTHORIZED);
    addDetail("requestedUserId", userId);
  }
}
