package com.part3.team07.sb01deokhugamteam07.dto.book;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookDto(
    UUID id,
    String title,
    String author,
    String description,
    String publisher,
    LocalDate publishedDate,
    String isbn,
    String thumbnailUrl,
    int reviewCount,
    int rating,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
