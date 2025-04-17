package com.part3.team07.sb01deokhugamteam07.dto.notification;

import java.time.LocalDateTime;

public record NotificationDto (
    UUID id,
    UUID userId,
    UUID reviewId,
    String reviewTitle,
    String content,
    boolean confirmed,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
){

}
