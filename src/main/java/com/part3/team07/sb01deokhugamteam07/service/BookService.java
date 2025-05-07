package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.client.NaverBookClient;
import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.NaverBookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.response.CursorPageResponseBookDto;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.FileType;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookAlreadyExistsException;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.book.InvalidSortFieldException;
import com.part3.team07.sb01deokhugamteam07.mapper.BookMapper;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
  private final ReviewService reviewService;

  private final ReviewRepository reviewRepository;

  private final NaverBookClient naverBookClient;

  @Transactional
  public BookDto create(BookCreateRequest request,
      MultipartFile thumbnailImage) {
    log.info("도서 생성 요청 시작. ISBN: {}", request.isbn());

    if (bookRepository.existsByIsbn(request.isbn())) {
      log.error("도서 생성 실패: 이미 존재하는 ISBN {}.", request.isbn());
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

    log.info("도서 생성 성공: {}", savedBook.getId());
    return bookMapper.toDto(savedBook);
  }

  public NaverBookDto getInfo(String isbn) {
    log.info("ISBN {}로 책 정보 조회.", isbn);

    return naverBookClient.searchByIsbn(isbn);
  }

  @Transactional
  public BookDto update(UUID id, BookUpdateRequest request, MultipartFile thumbnailImage) {
    log.info("도서 업데이트 요청 시작. ID: {}", id);

    Book book = bookRepository.findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> {
          log.error("도서 업데이트 실패: 존재하지 않거나 삭제된 ID {}", id);
          return BookNotFoundException.withId(id);
        });
    String thumbnailUrl = Optional.ofNullable(thumbnailImage)
        .map(image -> storageService.save(image, FileType.THUMBNAIL_IMAGE))
        .orElse(null);

    Optional.ofNullable(request.title()).ifPresent(book::updateTitle);
    Optional.ofNullable(request.author()).ifPresent(book::updateAuthor);
    Optional.ofNullable(request.description()).ifPresent(book::updateDescription);
    Optional.ofNullable(request.publisher()).ifPresent(book::updatePublisher);
    Optional.ofNullable(request.publishedDate()).ifPresent(book::updatePublishDate);
    Optional.ofNullable(thumbnailUrl).ifPresent(book::updateThumbnailUrl);

    log.info("도서 업데이트 성공: ID {}", book.getId());
    return bookMapper.toDto(book);
  }

  @Transactional
  public void softDelete(UUID id) {
    log.info("도서 논리 삭제 요청 시작. ID: {}", id);

    Book book = bookRepository.findById(id)
        .orElseThrow(() -> {
          log.error("도서 논리 삭제 실패: 존재하지 않는 ID {}", id);
          return BookNotFoundException.withId(id);
        });

    book.softDelete();
    reviewService.softDeleteAllByBook(book);

    log.info("도서 논리 삭제 성공: ID {}", book.getId());
  }

  @Transactional
  public void hardDelete(UUID id) {
    log.info("도서 물리 삭제 요청 시작. ID: {}", id);

    if (!bookRepository.existsById(id)) {
      log.error("도서 물리 삭제 실패: 존재하지 않는 ID {}", id);
      throw BookNotFoundException.withId(id);
    }

    bookRepository.deleteById(id);

    log.info("도서 물리 삭제 성공: ID {}", id);
  }

  @Transactional(readOnly = true)
  public BookDto find(UUID id) {
    log.info("도서 조회 요청 시작. ID: {}", id);

    Book book = bookRepository.findById(id)
        .orElseThrow(() -> {
          log.error("도서 조회 실패: 존재하지 않는 ID {}", id);
          return BookNotFoundException.withId(id);
        });

    log.info("도서 조회 성공: ID {}", book.getId());
    return bookMapper.toDto(book);
  }

  @Transactional(readOnly = true)
  public CursorPageResponseBookDto findAll(String keyword, String orderBy, String direction,
      String cursor, LocalDateTime after, int size) {
    log.info("도서 목록 조회 요청이 시작. 키워드: {}, 정렬 필드: {}, 방향: {}, 페이지 크기: {}", keyword, orderBy, direction, size);

    orderBy = orderBy != null ? orderBy : "title";
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

    log.info("도서 목록 조회 성공. 전체 개수: {}", totalElements);

    return new CursorPageResponseBookDto(
        bookDtos,
        nextCursor,
        nextAfter,
        size,
        totalElements,
        hasNext
    );
  }

  @Transactional
  public void updateReviewStats() {
    log.info("도서 리뷰 통계 업데이트 시작.");

    List<Book> books = bookRepository.findAll();

    for (Book book : books) {
      List<Review> reviews = reviewRepository.findAllByBook(book);

      if (reviews.isEmpty())
        continue;

      BigDecimal sum = reviews.stream()
          .map(review -> BigDecimal.valueOf(review.getRating()))
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      BigDecimal average = sum.divide(
          BigDecimal.valueOf(reviews.size()),
          1,
          RoundingMode.HALF_UP
      );

      int reviewCount = reviews.size();
      book.updateReviewStats(reviewCount, average);

      log.info("도서 리뷰 통계 업데이트 성공: ID {}", book.getId());
    }
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
      log.error("잘못된 정렬 필드: {}", sortField);
      throw InvalidSortFieldException.withField(sortField);
    }
  }
}
