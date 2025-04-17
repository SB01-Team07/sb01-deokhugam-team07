package com.part3.team07.sb01deokhugamteam07.dto.notification.response;

import java.util.List;
import com.part3.team07.sb01deokhugamteam07.entity.Notification;

public record CursorPageResponseNotificationDto (
    List<Notification> content,
    String nextCursor,
    String nextAfter,
    int size,
    int totalElements,
    boolean hasNext
){

}
