package com.part3.team07.sb01deokhugamteam07.dto.comment.response;

import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseCommentDto(
    List<CommentDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {
}
