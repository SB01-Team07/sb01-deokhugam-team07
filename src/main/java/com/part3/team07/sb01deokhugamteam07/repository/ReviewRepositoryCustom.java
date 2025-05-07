package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.type.ReviewDirection;
import com.part3.team07.sb01deokhugamteam07.type.ReviewOrderBy;
import com.querydsl.core.Tuple;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReviewRepositoryCustom {
    List<ReviewDto> findAll(UUID userId, UUID bookId, String keyword, ReviewOrderBy orderBy, ReviewDirection direction,
                             String cursor, LocalDateTime after, int limit, UUID requestUserId);

    long count(UUID userId, UUID bookId, String keyword);

    List<Review> findChangedSince(LocalDateTime cutoff);
}
