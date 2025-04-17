package com.part3.team07.sb01deokhugamteam07.dto.review.response;

import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseReviewDto(
    List<ReviewDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    int totalElements,
    boolean hasNext
) {

}
