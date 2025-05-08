package com.part3.team07.sb01deokhugamteam07.controller;

import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.NaverBookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.response.CursorPageResponseBookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.response.CursorPageResponsePopularBookDto;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.service.BookService;
import com.part3.team07.sb01deokhugamteam07.service.DashboardService;
import com.part3.team07.sb01deokhugamteam07.service.OcrService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

  private final BookService bookService;
  private final DashboardService dashboardService;
  private final OcrService ocrService;

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<BookDto> create(
      @RequestPart("bookData") @Valid BookCreateRequest request,
      @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage) {
    log.info("도서 생성 요청: {}", request);
    BookDto bookDto = bookService.create(request, thumbnailImage);
    log.debug("도서 생성 응답: {}", bookDto);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(bookDto);
  }

  @PatchMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<BookDto> update(
      @PathVariable UUID id,
      @RequestPart("bookData") @Valid BookUpdateRequest request,
      @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage) {
    log.info("도서 수정 요청: id={}, request={}", id, request);
    BookDto bookDto = bookService.update(id, request, thumbnailImage);
    log.debug("도서 수정 응답: {}", bookDto);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(bookDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> softDelete(@PathVariable UUID id) {
    log.info("도서 논리 삭제 요청: id={}", id);
    bookService.softDelete(id);
    log.debug("도서 논리 삭제 완료");

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @DeleteMapping("/{id}/hard")
  public ResponseEntity<Void> hardDelete(@PathVariable UUID id) {
    log.info("도서 물리 삭제 요청: id={}", id);
    bookService.hardDelete(id);
    log.debug("도서 물리 삭제 완료");

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookDto> find(@PathVariable UUID id) {
    log.info("도서 상세 정보 조회 요청: id={}", id);
    BookDto bookDto = bookService.find(id);
    log.debug("도서 상세 정보 조회 응답: {}", bookDto);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(bookDto);
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseBookDto> findAll(
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "title") String orderBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
      @RequestParam(defaultValue = "50") int limit) {
    log.info("도서 목록 조회 요청: keyword={}, orderBy={}, direction={}, cursor={}, after={}, limit={}",
        keyword, orderBy, direction, cursor, after, limit);
    CursorPageResponseBookDto bookDto = bookService.findAll(keyword, orderBy, direction, cursor,
        after, limit);
    log.debug("도서 목록 조회 응답: {}", bookDto);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(bookDto);
  }

  @GetMapping("/popular")
  public ResponseEntity<CursorPageResponsePopularBookDto> findPopularBooks(
      @RequestParam Period period,
      @RequestParam(required = false, defaultValue = "asc") @Pattern(regexp = "(?i)ASC|DESC", message = "direction은 ASC 또는 DESC만 가능합니다.") String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) String after,
      @RequestParam(required = false, defaultValue = "50") @Min(1) int limit
  ) {
    log.info("인기 도서 목록 조회 요청: period={}, direction={}, cursor={}, after={}, limit={}",
        period, direction, cursor, after, limit);
    CursorPageResponsePopularBookDto popularBookDto = dashboardService.getPopularBooks(period, direction, cursor, after, limit);
    log.debug("인기 도서 목록 조회 응답: {}", popularBookDto);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(popularBookDto);
  }

  @GetMapping("/info")
  public ResponseEntity<NaverBookDto> getInfo(@RequestParam String isbn) {
    log.info("ISBN으로 도서 정보 조회 요청: isbn={}", isbn);
    NaverBookDto naverBookDto = bookService.getInfo(isbn);
    log.debug("ISBN으로 도서 정보 조회 응답: {}", naverBookDto);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(naverBookDto);
  }

  @PostMapping(value = "/isbn/ocr", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<String> extractIsbnByOcr(@RequestPart("image") MultipartFile image) {
    log.info("이미지 기반 ISBN 인식 요청");
    String isbn = ocrService.extractIsbn13(image);
    log.info("이미지 기반 ISBN 인식 응답: isbn={}", isbn);

    return ResponseEntity.ok(isbn);
  }
}
