package com.part3.team07.sb01deokhugamteam07.dto.book;

import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PopularBookDto(
    UUID id,
    UUID bookId,
    String title,
    String author,
    String thumbnailUrl,
    Period period,
    int rank,
    BigDecimal score,
    int reviewCount,
    BigDecimal rating,
    LocalDateTime createdAt
) {
}
