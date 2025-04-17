package com.part3.team07.sb01deokhugamteam07.dto.comment;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(
    UUID id,
    UUID reviewId,
    UUID userId,
    String userNickname,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
