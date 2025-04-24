package com.part3.team07.sb01deokhugamteam07.dto.book.request;

import com.part3.team07.sb01deokhugamteam07.validator.NullableNotBlank;
import java.time.LocalDate;

public record BookUpdateRequest(
    @NullableNotBlank
    String title,

    @NullableNotBlank
    String author,

    @NullableNotBlank
    String description,

    @NullableNotBlank
    String publisher,

    LocalDate publishedDate
) {

}
