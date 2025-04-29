package com.part3.team07.sb01deokhugamteam07.dto.notification.response;

import com.part3.team07.sb01deokhugamteam07.dto.notification.NotificationDto;
import java.util.List;
import lombok.Builder;

@Builder
public record CursorPageResponseNotificationDto (
    List<NotificationDto> content,
    String nextCursor,
    String nextAfter,
    int size,
    int totalElements,
    boolean hasNext
){

}
