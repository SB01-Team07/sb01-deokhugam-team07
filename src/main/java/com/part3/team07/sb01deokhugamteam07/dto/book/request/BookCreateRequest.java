package com.part3.team07.sb01deokhugamteam07.dto.book.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record BookCreateRequest(
    @NotBlank(message = "")
    String title,

    @NotBlank
    String author,

    @NotBlank
    String description,

    @NotBlank
    String publisher,

    @NotBlank
    LocalDate publishedDate,

    String isbn
) {

}
