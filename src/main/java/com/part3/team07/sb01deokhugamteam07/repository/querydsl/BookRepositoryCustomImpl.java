package com.part3.team07.sb01deokhugamteam07.repository.querydsl;

import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.QBook;
import com.part3.team07.sb01deokhugamteam07.util.TitleNormalizer;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final TitleNormalizer titleNormalizer;

  @Override
  public List<Book> findBooksWithCursor(String keyword, String orderBy, String direction,
      String cursor, LocalDateTime after, int size) {
    log.debug("도서 조회 시작 - keyword={}, orderBy={}, direction={}, cursor={}, after={}, size={}",
        keyword, orderBy, direction, cursor, after, size);

    QBook book = QBook.book;

    JPAQuery<Book> query = queryFactory.selectFrom(book)
        .where(book.isDeleted.eq(false))
        .limit(size);

    if (keyword != null && !keyword.isBlank()) {
      log.debug("키워드 검색 조건 추가 - keyword={}", keyword);
      query.where(
          book.title.containsIgnoreCase(keyword)
              .or(book.author.containsIgnoreCase(keyword))
              .or(book.isbn.containsIgnoreCase(keyword))
      );
    }

    if (cursor != null && after != null) {
      log.debug("커서 기반 페이지네이션 조건 추가 - orderBy={}, cursor={}", orderBy, cursor);
      query.where(createCursorPredicate(book, orderBy, direction, cursor, after));
    }

    applySorting(query, book, orderBy, direction);
    List<Book> results = query.fetch();

    log.debug("도서 조회 완료 - {}건 조회됨", results.size());

    return results;
  }

  @Override
  public long countByKeyword(String keyword) {
    log.debug("키워드 기준 도서 개수 조회 - keyword={}", keyword);

    QBook book = QBook.book;

    JPAQuery<Long> query = queryFactory.select(book.count())
        .from(book)
        .where(book.isDeleted.eq(false));

    if (keyword != null && !keyword.isBlank()) {
      log.debug("키워드 필터 적용 - keyword={}", keyword);
      query.where(
          book.title.containsIgnoreCase(keyword)
              .or(book.author.containsIgnoreCase(keyword))
              .or(book.isbn.containsIgnoreCase(keyword))
      );
    }

    Long count = query.fetchOne();

    log.debug("도서 개수 조회 결과 - count={}", count);

    return count != null ? count : 0L;
  }

  private BooleanExpression createCursorPredicate(QBook book, String orderBy, String direction,
      String cursor, LocalDateTime after) {
    log.debug("커서 조건 생성 시작 - direction={}, orderBy={}, cursor={}", direction, orderBy, cursor);

    if ("asc".equalsIgnoreCase(direction)) {
      return createAscCursorPredicate(book, orderBy, cursor, after);
    } else {
      return createDescCursorPredicate(book, orderBy, cursor, after);
    }
  }

  private String normalizeString(String input) {
    if (input == null) {
      return null;
    }
    String normalized = input.toLowerCase().replaceAll("[^가-힣a-z0-9]", "");

    log.debug("문자열 정규화 - 원본='{}', 정규화='{}'", input, normalized);

    return normalized;
  }

  private BooleanExpression createAscCursorPredicate(QBook book, String orderBy,
      String cursor, LocalDateTime after) {
    try {
      switch (orderBy) {
        case "title":
          String normalizedCursor = normalizeString(cursor);
          return titleNormalizer.getNormalizedTitle(book.title).gt(normalizedCursor).or(
              titleNormalizer.getNormalizedTitle(book.title).eq(normalizedCursor).and(book.createdAt.lt(after))
          );
        case "publishedDate":
          LocalDate cursorDate = LocalDate.parse(cursor);
          return book.publishDate.gt(cursorDate).or(
              book.publishDate.eq(cursorDate).and(book.createdAt.lt(after))
          );
        case "rating":
          double ratingValue = Double.parseDouble(cursor);
          return book.rating.gt(ratingValue).or(
              book.rating.eq(BigDecimal.valueOf(ratingValue)).and(book.createdAt.lt(after))
          );
        case "reviewCount":
          int reviewCountValue = Integer.parseInt(cursor);
          return book.reviewCount.gt(reviewCountValue).or(
              book.reviewCount.eq(reviewCountValue).and(book.createdAt.lt(after))
          );
        default:
          log.warn("알 수 없는 orderBy 값 - {}", orderBy);
          return null;
      }
    } catch (Exception e) {
      log.warn("ASC 커서 조건 생성 실패 - orderBy={}, cursor={}, 에러={}", orderBy, cursor, e.getMessage());
      return null;
    }
  }

  private BooleanExpression createDescCursorPredicate(QBook book, String orderBy,
      String cursor, LocalDateTime after) {
    try {
      switch (orderBy) {
        case "title":
          String normalizedCursor = normalizeString(cursor);
          return titleNormalizer.getNormalizedTitle(book.title).lt(normalizedCursor).or(
              titleNormalizer.getNormalizedTitle(book.title).eq(normalizedCursor).and(book.createdAt.lt(after))
          );
        case "publishedDate":
          LocalDate cursorDate = LocalDate.parse(cursor);
          return book.publishDate.lt(cursorDate).or(
              book.publishDate.eq(cursorDate).and(book.createdAt.lt(after))
          );
        case "rating":
          double ratingValue = Double.parseDouble(cursor);
          return book.rating.lt(ratingValue).or(
              book.rating.eq(BigDecimal.valueOf(ratingValue)).and(book.createdAt.lt(after))
          );
        case "reviewCount":
          int reviewCountValue = Integer.parseInt(cursor);
          return book.reviewCount.lt(reviewCountValue).or(
              book.reviewCount.eq(reviewCountValue).and(book.createdAt.lt(after))
          );
        default:
          log.warn("알 수 없는 orderBy 값 - {}", orderBy);
          return null;
      }
    } catch (Exception e) {
      log.warn("DESC 커서 조건 생성 실패 - orderBy={}, cursor={}, 에러={}", orderBy, cursor, e.getMessage());
      return null;
    }
  }

  private void applySorting(JPAQuery<Book> query, QBook book, String orderBy, String direction) {
    boolean isAsc = "asc".equalsIgnoreCase(direction);
    log.debug("정렬 조건 적용 - orderBy={}, direction={}", orderBy, direction);

    OrderSpecifier<?> primary;

    switch (orderBy) {
      case "title":
        if (isAsc) {
          primary = titleNormalizer.getNormalizedTitle(book.title).asc();
        } else {
          primary = titleNormalizer.getNormalizedTitle(book.title).desc();
        }
        break;
      case "publishedDate":
        primary = isAsc ? book.publishDate.asc() : book.publishDate.desc();
        break;
      case "rating":
        primary = isAsc ? book.rating.asc() : book.rating.desc();
        break;
      case "reviewCount":
        primary = isAsc ? book.reviewCount.asc() : book.reviewCount.desc();
        break;
      default:
        primary = book.title.desc();
        log.warn("잘못된 orderBy 값 '{}', 기본값(title 내림차순)으로 정렬", orderBy);
    }

    query.orderBy(primary, book.createdAt.desc());
  }
}