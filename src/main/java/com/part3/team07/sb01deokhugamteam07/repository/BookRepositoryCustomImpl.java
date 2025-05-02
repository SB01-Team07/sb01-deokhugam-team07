package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.QBook;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Book> findBooksWithCursor(String keyword, String orderBy, String direction,
      String cursor, LocalDateTime after, int size) {
    QBook book = QBook.book;

    JPAQuery<Book> query = queryFactory.selectFrom(book)
        .where(book.isDeleted.eq(false))
        .limit(size);

    if (keyword != null && !keyword.isBlank()) {
      query.where(
          book.title.containsIgnoreCase(keyword)
              .or(book.author.containsIgnoreCase(keyword))
              .or(book.isbn.containsIgnoreCase(keyword))
      );
    }

    if (cursor != null && after != null) {
      query.where(createCursorPredicate(book, orderBy, direction, cursor, after));
    }

    applySorting(query, book, orderBy, direction);

    return query.fetch();
  }

  @Override
  public long countByKeyword(String keyword) {
    QBook book = QBook.book;

    JPAQuery<Long> query = queryFactory.select(book.count())
        .from(book)
        .where(book.isDeleted.eq(false));

    if (keyword != null && !keyword.isBlank()) {
      query.where(
          book.title.containsIgnoreCase(keyword)
              .or(book.author.containsIgnoreCase(keyword))
              .or(book.isbn.containsIgnoreCase(keyword))
      );
    }

    return query.fetchOne();
  }

  private BooleanExpression createCursorPredicate(QBook book, String orderBy, String direction,
      String cursor, LocalDateTime after) {
    if ("asc".equalsIgnoreCase(direction)) {
      return createAscCursorPredicate(book, orderBy, cursor, after);
    } else {
      return createDescCursorPredicate(book, orderBy, cursor, after);
    }
  }

  private BooleanExpression createAscCursorPredicate(QBook book, String orderBy,
      String cursor, LocalDateTime after) {
    switch (orderBy) {
      case "title":
        return book.title.gt(cursor).or(
            book.title.eq(cursor).and(book.createdAt.lt(after))
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
        return null;
    }
  }

  private BooleanExpression createDescCursorPredicate(QBook book, String orderBy,
      String cursor, LocalDateTime after) {
    switch (orderBy) {
      case "title":
        return book.title.lt(cursor).or(
            book.title.eq(cursor).and(book.createdAt.lt(after))
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
        return null;
    }
  }

  private void applySorting(JPAQuery<Book> query, QBook book, String orderBy, String direction) {
    boolean isAsc = "asc".equalsIgnoreCase(direction);

    OrderSpecifier<?> primary = switch (orderBy) {
      case "title" -> isAsc ? book.title.asc() : book.title.desc();
      case "publishedDate" -> isAsc ? book.publishDate.asc() : book.publishDate.desc();
      case "rating" -> isAsc ? book.rating.asc() : book.rating.desc();
      case "reviewCount" -> isAsc ? book.reviewCount.asc() : book.reviewCount.desc();
      default -> book.title.desc();
    };

    query.orderBy(primary, book.createdAt.desc());
  }
}
