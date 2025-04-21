package com.part3.team07.sb01deokhugamteam07.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CommentCreateRequest(
    @NotNull(message = "리뷰 ID는 필수입니다.")
    UUID reviewId,
    @NotNull(message = "사용자 ID는 필수입니다.")
    UUID userId,
    @NotBlank(message = "내용은 필수입니다.")
    String content
) {
}
