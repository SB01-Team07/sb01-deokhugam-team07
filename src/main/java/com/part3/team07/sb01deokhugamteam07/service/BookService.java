package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.response.CursorPageResponseBookDto;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.FileType;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookAlreadyExistsException;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.book.InvalidSortFieldException;
import com.part3.team07.sb01deokhugamteam07.mapper.BookMapper;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;

  private final StorageService storageService;

  @Transactional
  public BookDto create(BookCreateRequest request,
      MultipartFile thumbnailImage) {
    if (bookRepository.existsByIsbn(request.isbn())) {
      throw BookAlreadyExistsException.withIsbn(request.isbn());
    }
    String thumbnailUrl = Optional.ofNullable(thumbnailImage)
        .map(image -> storageService.save(image, FileType.THUMBNAIL_IMAGE))
        .orElse(null);

    Book book = Book.builder()
        .title(request.title())
        .author(request.author())
        .description(request.description())
        .publisher(request.publisher())
        .publishDate(request.publishedDate())
        .isbn(request.isbn())
        .thumbnailUrl(thumbnailUrl)
        .build();
    Book savedBook = bookRepository.save(book);

    return bookMapper.toDto(savedBook);
  }

  @Transactional
  public BookDto update(UUID id, BookUpdateRequest request, MultipartFile thumbnailImage) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> BookNotFoundException.withId(id));
    String thumbnailUrl = Optional.ofNullable(thumbnailImage)
        .map(image -> storageService.save(image, FileType.THUMBNAIL_IMAGE))
        .orElse(null);

    Optional.ofNullable(request.title()).ifPresent(book::updateTitle);
    Optional.ofNullable(request.author()).ifPresent(book::updateAuthor);
    Optional.ofNullable(request.description()).ifPresent(book::updateDescription);
    Optional.ofNullable(request.publisher()).ifPresent(book::updatePublisher);
    Optional.ofNullable(request.publishedDate()).ifPresent(book::updatePublishDate);
    Optional.ofNullable(thumbnailUrl).ifPresent(book::updateThumbnailUrl);

    return bookMapper.toDto(book);
  }

  @Transactional
  public void softDelete(UUID id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> BookNotFoundException.withId(id));

    book.softDelete();
    // TODO: 관련 엔티티 논리 삭제
  }

  @Transactional
  public void hardDelete(UUID id) {
    if (!bookRepository.existsById(id)) {
      throw BookNotFoundException.withId(id);
    }

    bookRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public BookDto find(UUID id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> BookNotFoundException.withId(id));

    return bookMapper.toDto(book);
  }

  @Transactional(readOnly = true)
  public CursorPageResponseBookDto findAll(String keyword, String orderBy, String direction,
      String cursor, LocalDateTime after, int size) {
    orderBy = orderBy != null ? orderBy : "publishedDate";
    direction = direction != null ? direction : "desc";
    size = size > 0 ? size : 50;

    validateSortField(orderBy);

    List<Book> books = bookRepository.findBooksWithCursor(
        keyword, orderBy, direction, cursor, after, size + 1);
    boolean hasNext = books.size() > size;

    if (hasNext) {
      books = books.subList(0, size);
    }

    List<BookDto> bookDtos = books.stream()
        .map(bookMapper::toDto)
        .collect(Collectors.toList());

    String nextCursor = null;
    LocalDateTime nextAfter = null;

    if (hasNext && !books.isEmpty()) {
      Book lastBook = books.get(books.size() - 1);
      nextCursor = getCursorValue(lastBook, orderBy);
      nextAfter = lastBook.getCreatedAt();
    }

    long totalElements = bookRepository.countByKeyword(keyword);

    return new CursorPageResponseBookDto(
        bookDtos,
        nextCursor,
        nextAfter,
        size,
        totalElements,
        hasNext
    );
  }

  private String getCursorValue(Book book, String sortField) {
    return switch (sortField) {
      case "title" -> book.getTitle();
      case "publishedDate" -> book.getPublishDate().toString();
      case "rating" -> String.valueOf(book.getRating());
      case "reviewCount" -> String.valueOf(book.getReviewCount());
      default -> book.getPublishDate().toString();
    };
  }

  private void validateSortField(String sortField) {
    List<String> validSortFields = List.of("title", "publishedDate", "rating", "reviewCount");
    if (!validSortFields.contains(sortField)) {
      throw InvalidSortFieldException.withField(sortField);
    }
  }
}
