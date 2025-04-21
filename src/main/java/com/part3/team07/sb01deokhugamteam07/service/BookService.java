package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookCreateRequest;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;

  public BookDto create(BookCreateRequest request) {
    if (bookRepository.existsByIsbn(request.isbn())) {
      throw new DuplicateIsbnException();
    }

    Book book = new Book(
        "title",
        "author",
        "description",
        "publisher",
        LocalDate.now(),
        "isbn",
        "thumbnail"
    );
    Book savedBook = bookRepository.save(book);

    return bookMapper.toDto(savedBook);
  }
}
