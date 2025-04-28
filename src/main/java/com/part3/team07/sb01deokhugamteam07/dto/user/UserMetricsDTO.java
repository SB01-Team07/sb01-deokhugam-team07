package com.part3.team07.sb01deokhugamteam07.dto.user;

import java.math.BigDecimal;
import java.util.UUID;


public record UserMetricsDTO(
    UUID userId,
    BigDecimal reviewScoreSum,
    BigDecimal likeCount,
    BigDecimal commentCount
) {

}
