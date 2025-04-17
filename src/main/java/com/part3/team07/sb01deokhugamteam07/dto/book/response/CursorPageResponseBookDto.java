package com.part3.team07.sb01deokhugamteam07.dto.book.response;

import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseBookDto(
    List<BookDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
