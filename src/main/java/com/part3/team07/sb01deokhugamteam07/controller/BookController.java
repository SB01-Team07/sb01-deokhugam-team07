package com.part3.team07.sb01deokhugamteam07.controller;

import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.service.BookService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

  private final BookService bookService;

  @PostMapping
  public ResponseEntity<BookDto> create(@RequestPart("bookData") @Valid BookCreateRequest request,
      @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage) {
    BookDto bookDto = bookService.create(request, thumbnailImage);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(bookDto);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<BookDto> update(@PathVariable UUID id,
      @RequestBody @Valid BookUpdateRequest request) {
    BookDto bookDto = bookService.update(id, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(bookDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> softDelete(@PathVariable UUID id) {
    bookService.softDelete(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @DeleteMapping("/{id}/hard")
  public ResponseEntity<Void> hardDelete(@PathVariable UUID id) {
    bookService.hardDelete(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
