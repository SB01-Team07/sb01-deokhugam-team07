package com.part3.team07.sb01deokhugamteam07.dto.book;

import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.time.LocalDateTime;
import java.util.UUID;

public record PopularBookDto(
    UUID id,
    UUID bookId,
    String title,
    String author,
    String thumbnailFileName,
    Period period,
    int rank,
    double score,
    int reviewCount,
    double rating,
    LocalDateTime createdAt
) {
}
