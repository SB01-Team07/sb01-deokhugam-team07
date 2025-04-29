package com.part3.team07.sb01deokhugamteam07.dto.notification.request;

import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationCreateRequest {
  private NotificationType type;
  private UUID senderId;
  private UUID reviewId;
  private Period period;
  private int rank;
}
