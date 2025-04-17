package com.part3.team07.sb01deokhugamteam07.dto.book.request;

import java.time.LocalDate;

public record BookCreateRequest(
    String title,
    String author,
    String description,
    String publisher,
    LocalDate publishedDate,
    String isbn
) {

}
