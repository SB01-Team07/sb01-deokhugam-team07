package com.part3.team07.sb01deokhugamteam07.dto.user.response;

import com.part3.team07.sb01deokhugamteam07.dto.user.PowerUserDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record CursorPageResponsePowerUserDto(
    List<PowerUserDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}