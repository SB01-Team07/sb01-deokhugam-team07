package com.part3.team07.sb01deokhugamteam07.dto.comment.response;

import java.util.List;

public record CursorPageResponseCommentDto(
    List<?> content,
    String nextCursor,
    String nextAfter,
    int size,
    int totalElements,
    boolean hasNext
) {
}
