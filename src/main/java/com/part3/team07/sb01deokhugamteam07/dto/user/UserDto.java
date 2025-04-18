package com.part3.team07.sb01deokhugamteam07.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserDto (
    UUID id,
    String email,
    String nickname,
    LocalDateTime createdAt
) {

}
