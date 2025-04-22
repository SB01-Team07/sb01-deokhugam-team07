package com.part3.team07.sb01deokhugamteam07.dto.user;

import java.util.UUID;


public record UserMetricsDTO(
    UUID userId,
    Double reviewScoreSum,
    Double likeCount,
    Double commentCount
) {

}
