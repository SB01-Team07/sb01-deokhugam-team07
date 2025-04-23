package com.part3.team07.sb01deokhugamteam07.dto.comment.request;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(
    @NotBlank(message = "내용은 필수입니다.")
    String content
) {
}
