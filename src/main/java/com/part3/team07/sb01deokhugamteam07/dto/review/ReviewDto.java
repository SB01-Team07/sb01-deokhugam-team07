package com.part3.team07.sb01deokhugamteam07.dto.review;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewDto(
    UUID id,
    UUID bookId,
    String bookTitle,
    String bookThumbnailUrl,
    UUID userId,
    String userNickName,
    String content,
    int rating,
    int likeCount,
    int commentCount,
    boolean likeByMe,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
