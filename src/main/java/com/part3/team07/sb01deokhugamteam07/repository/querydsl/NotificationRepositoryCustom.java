package com.part3.team07.sb01deokhugamteam07.repository.querydsl;

import com.part3.team07.sb01deokhugamteam07.entity.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationRepositoryCustom {

  List<Notification> findByUserIdWithCursor(UUID userId, String direction, String cursor, String after, int limit);
}
