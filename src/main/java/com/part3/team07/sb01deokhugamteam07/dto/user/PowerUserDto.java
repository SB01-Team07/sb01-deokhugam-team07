package com.part3.team07.sb01deokhugamteam07.dto.user;

import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PowerUserDto (
    UUID userId,
    String nickname,
    Period period,
    LocalDateTime createdAt,
    int rank,
    double score,
    double reviewScoreSum,
    int likeCount,
    int commentCount
) {

}
