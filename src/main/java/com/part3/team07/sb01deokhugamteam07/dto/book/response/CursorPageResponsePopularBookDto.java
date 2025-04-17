package com.part3.team07.sb01deokhugamteam07.dto.book.response;

import com.part3.team07.sb01deokhugamteam07.dto.book.PopularBookDto;
import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponsePopularBookDto(
    List<PopularBookDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
