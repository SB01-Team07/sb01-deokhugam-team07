package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.exception.book.DuplicateIsbnException;
import com.part3.team07.sb01deokhugamteam07.mapper.BookMapper;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;

  private final ThumbnailImageService thumbnailImageService;

  public BookDto create(BookCreateRequest request,
      MultipartFile thumbnailImage) {
    if (bookRepository.existsByIsbn(request.isbn())) {
      throw new DuplicateIsbnException();
    }
    String thumbnailFileName = thumbnailImageService.save(thumbnailImage);

    Book book = Book.builder()
        .title(request.title())
        .author(request.author())
        .description(request.description())
        .publisher(request.publisher())
        .publishDate(request.publishedDate())
        .isbn(request.isbn())
        .thumbnailFileName(thumbnailFileName)
        .build();
    Book savedBook = bookRepository.save(book);

    return bookMapper.toDto(savedBook);
  }

  public BookDto update(UUID id, BookUpdateRequest request) {
    bookRepository.findById(id);
    Book book = Book.builder()
        .title(request.title())
        .author(request.author())
        .description(request.description())
        .publisher(request.publisher())
        .publishDate(request.publishedDate())
        .isbn("")
        .thumbnailFileName("")
        .build();
    Book savedBook = bookRepository.save(book);

    return bookMapper.toDto(savedBook);
  }
}
