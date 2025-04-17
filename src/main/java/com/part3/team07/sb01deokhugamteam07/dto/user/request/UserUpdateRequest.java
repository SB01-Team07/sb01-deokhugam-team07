package com.part3.team07.sb01deokhugamteam07.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserUpdateRequest (
    @NotBlank(message = "닉네임을 적어주세요.")
    @Length(min = 2, max = 20)
    String nickname
) {}
