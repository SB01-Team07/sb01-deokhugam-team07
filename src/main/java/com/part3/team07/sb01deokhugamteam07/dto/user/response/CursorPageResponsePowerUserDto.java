package com.part3.team07.sb01deokhugamteam07.dto.user.response;

import java.util.List;

public record CursorPageResponsePowerUserDto(
    List<?> content,
    String nextCursor,
    String nextAfter,
    Integer size,
    Long totalElements,
    Boolean hasNext
) { }