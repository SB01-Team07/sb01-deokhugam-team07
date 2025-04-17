package com.part3.team07.sb01deokhugamteam07.dto.user.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequest (
    @NotBlank(message = "이메일은 필수입니다.")
    @Column(unique = true, nullable = false)
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,

    @Column(nullable = false)
    @Length(min = 8, max = 20)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    String password
) {}