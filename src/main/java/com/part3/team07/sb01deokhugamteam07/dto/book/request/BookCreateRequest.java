package com.part3.team07.sb01deokhugamteam07.dto.book.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookCreateRequest(
    @NotBlank(message = "제목을 입력해주세요.")
    String title,

    @NotBlank(message = "저자를 입력해주세요.")
    String author,

    @NotBlank(message = "설명을 입력해주세요.")
    String description,

    @NotBlank(message = "출판사를 입력해주세요.")
    String publisher,

    @NotNull(message = "출판일을 입력해주세요.")
    LocalDate publishedDate,

    String isbn
) {

}
