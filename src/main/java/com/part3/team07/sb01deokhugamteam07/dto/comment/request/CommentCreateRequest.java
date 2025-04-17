package com.part3.team07.sb01deokhugamteam07.dto.comment.request;

import java.util.UUID;

public record CommentCreateRequest(
    UUID reviewId,
    UUID userId,
    String content
) {
}
