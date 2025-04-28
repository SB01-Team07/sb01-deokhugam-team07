package com.part3.team07.sb01deokhugamteam07.dto.review;

import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PopularReviewDto (
    UUID id,
    UUID reviewId,
    UUID bookId,
    String bookTitle,
    String bookThumbnailUrl,
    UUID userId,
    String userNickname,
    String reviewContent,
    BigDecimal reviewRating,
    Period period,
    LocalDateTime createdAt,
    int rank,
    BigDecimal score,
    int likeCount,
    int commentCount
){

}
