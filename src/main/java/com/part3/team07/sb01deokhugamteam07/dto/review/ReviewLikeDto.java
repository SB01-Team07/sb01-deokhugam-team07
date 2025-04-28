package com.part3.team07.sb01deokhugamteam07.dto.review;

import java.util.UUID;

public record ReviewLikeDto(
    UUID reviewId,
    UUID userId,
    boolean liked
) {

}
