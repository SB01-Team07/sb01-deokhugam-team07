package com.part3.team07.sb01deokhugamteam07.dto.review;

public record ReviewDto(
    String id,
    String bookId,
    String bookTitle,
    String bookThumbnailUrl,
    String userId,
    String userNickName,
    String content,
    int rating,
    int likeCount,
    int commentCount,
    boolean likeByMe,
    String createdAt,
    String updatedAt
) {

}
