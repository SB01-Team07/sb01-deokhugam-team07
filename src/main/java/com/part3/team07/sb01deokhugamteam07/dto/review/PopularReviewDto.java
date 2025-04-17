package com.part3.team07.sb01deokhugamteam07.dto.review;

import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.time.LocalDateTime;

public record PopularReviewDto (
    String id,
    String reviewId,
    String bookId,
    String bookTitle,
    String bookThumbnailUrl,
    String userId,
    String userNickname,
    String reviewContent,
    double number,
    Period period,
    LocalDateTime createdAt,
    int rank,
    double score,
    int likeCount,
    int commentCount
){

}
