package com.part3.team07.sb01deokhugamteam07.dto.review.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewUpdateRequest(
        @NotBlank(message = "리뷰 내용은 비워둘 수 없습니다.")
        String content,

        @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5 이하여야 합니다.")
        int rating
) {
}
