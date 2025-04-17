package com.part3.team07.sb01deokhugamteam07.dto.review.response;

import com.part3.team07.sb01deokhugamteam07.dto.review.PopularReviewDto;
import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponsePopularReviewDto(
    List<PopularReviewDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
