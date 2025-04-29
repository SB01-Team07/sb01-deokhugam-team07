package com.part3.team07.sb01deokhugamteam07.dto.book.request;

import com.part3.team07.sb01deokhugamteam07.validator.NullableNotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookUpdateRequest(
    @NullableNotBlank(message = "제목을 입력해주세요.")
    String title,

    @NullableNotBlank(message = "저자를 입력해주세요.")
    String author,

    @NullableNotBlank(message = "설명을 입력해주세요.")
    String description,

    @NullableNotBlank(message = "출판사를 입력해주세요.")
    String publisher,

    @NotNull(message = "출판일을 입력해주세요.")
    LocalDate publishedDate
) {

}
