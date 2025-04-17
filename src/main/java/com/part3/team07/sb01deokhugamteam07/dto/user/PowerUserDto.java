package com.part3.team07.sb01deokhugamteam07.dto.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PowerUserDto (
    UUID userId,
    String nickname,
    String period,
    LocalDateTime createdAt,
    Integer rank,
    BigDecimal reviewScoreSum,
    Integer lickCount,
    Integer commentCount
) {

}
