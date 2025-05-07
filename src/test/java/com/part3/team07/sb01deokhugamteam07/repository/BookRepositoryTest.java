package com.part3.team07.sb01deokhugamteam07.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.part3.team07.sb01deokhugamteam07.config.QuerydslConfig;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.repository.querydsl.BookRepositoryCustomImpl;
import com.part3.team07.sb01deokhugamteam07.util.TitleNormalizer;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@Import({BookRepositoryCustomImpl.class, QuerydslConfig.class, TitleNormalizer.class})
class BookRepositoryTest {

  @Autowired
  private BookRepository bookRepository;

  private Book book1, book2, book3, book4, book5;

  @BeforeEach
  void setUp() {
    book1 = Book.builder()
        .title("Harry Potter")
        .author("J.K. Rowling")
        .description("Fantasy book about wizards")
        .publisher("Publisher A")
        .publishDate(LocalDate.of(1997, 6, 26))
        .isbn("1234567890")
        .thumbnailUrl(null)
        .reviewCount(1000)
        .rating(BigDecimal.valueOf(4.5))
        .build();

    book2 = Book.builder()
        .title("The Hobbit")
        .author("J.R.R. Tolkien")
        .description("Adventure of Bilbo Baggins")
        .publisher("Publisher B")
        .publishDate(LocalDate.of(1937, 9, 21))
        .isbn("2345678901")
        .thumbnailUrl(null)
        .reviewCount(2000)
        .rating(BigDecimal.valueOf(4.7))
        .build();

    book3 = Book.builder()
        .title("1984")
        .author("George Orwell")
        .description("Dystopian novel")
        .publisher("Publisher C")
        .publishDate(LocalDate.of(1949, 6, 8))
        .isbn("3456789012")
        .thumbnailUrl(null)
        .reviewCount(1500)
        .rating(BigDecimal.valueOf(4.6))
        .build();

    book4 = Book.builder()
        .title("Pride and Prejudice")
        .author("Jane Austen")
        .description("Romance novel")
        .publisher("Publisher D")
        .publishDate(LocalDate.of(1813, 1, 28))
        .isbn("4567890123")
        .thumbnailUrl(null)
        .reviewCount(800)
        .rating(BigDecimal.valueOf(4.4))
        .build();

    book5 = Book.builder()
        .title("The Great Gatsby")
        .author("F. Scott Fitzgerald")
        .description("American classic")
        .publisher("Publisher E")
        .publishDate(LocalDate.of(1925, 4, 10))
        .isbn("5678901234")
        .thumbnailUrl(null)
        .reviewCount(1200)
        .rating(BigDecimal.valueOf(4.3))
        .build();

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5));

    setField(book1, "createdAt",
        LocalDateTime.of(2023, 1, 1, 0, 0));
    setField(book2, "createdAt",
        LocalDateTime.of(2023, 1, 2, 0, 0));
    setField(book3, "createdAt",
        LocalDateTime.of(2023, 1, 3, 0, 0));
    setField(book4, "createdAt",
        LocalDateTime.of(2023, 1, 4, 0, 0));
    setField(book5, "createdAt",
        LocalDateTime.of(2023, 1, 5, 0, 0));
  }

  private void setField(Object target, String fieldName, Object value) {
    try {
      Field field = findField(target.getClass(), fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
    while (clazz != null) {
      try {
        return clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    }
    throw new NoSuchFieldException(fieldName);
  }

  @Test
  @DisplayName("키워드로 책 검색 성공")
  void findBooksWithCursor_success_WithKeyword() {
    // given
    String keyword = "Potter";

    // when
    List<Book> books = bookRepository.findBooksWithCursor(keyword, "title", "asc", null, null, 5);

    // then
    assertThat(books).hasSize(1);;
    assertThat(books.get(0).getTitle()).isEqualTo("Harry Potter");
  }

  @Test
  @DisplayName("제목 기준 오름차순으로 책 목록 조회 성공")
  void findBooksWithCursor_success_SortByTitleAsc() {
    // given
    String orderBy = "title";
    String direction = "asc";

    // when
    List<Book> books = bookRepository.findBooksWithCursor(null, orderBy, direction, null, null, 5);

    // then
    assertThat(books).hasSize(5);
    assertThat(books.get(0).getTitle()).isEqualTo("1984");
    assertThat(books.get(1).getTitle()).isEqualTo("Harry Potter");
    assertThat(books.get(2).getTitle()).isEqualTo("Pride and Prejudice");
    assertThat(books.get(3).getTitle()).isEqualTo("The Great Gatsby");
    assertThat(books.get(4).getTitle()).isEqualTo("The Hobbit");
  }

  @Test
  @DisplayName("출판일 기준 내림차순으로 책 목록 조회 성공")
  void findBooksWithCursor_success_SortByPublishedDateDesc() {
    // given
    String orderBy = "publishedDate";
    String direction = "desc";

    // when
    List<Book> books = bookRepository.findBooksWithCursor(null, orderBy, direction, null, null, 3);
    System.out.println("sorted books");
    for (Book book : books) {
      System.out.println(book.getTitle());
    }

    // then
    assertThat(books).hasSize(3);
    assertThat(books.get(0).getTitle()).isEqualTo("Harry Potter");
    assertThat(books.get(1).getTitle()).isEqualTo("1984");
    assertThat(books.get(2).getTitle()).isEqualTo("The Hobbit");
  }

  @Test
  @DisplayName("출판일 기준 오름차순으로 책 목록 조회 성공")
  void findBooksWithCursor_success_SortByPublishedDateAsc() {
    // given
    String sortField = "publishedDate";
    String sortOrder = "asc";

    // when
    List<Book> books = bookRepository.findBooksWithCursor(null, sortField, sortOrder, null, null, 3);

    // then
    assertThat(books).hasSize(3);
    assertThat(books.get(0).getTitle()).isEqualTo("Pride and Prejudice");
    assertThat(books.get(1).getTitle()).isEqualTo("The Great Gatsby");
    assertThat(books.get(2).getTitle()).isEqualTo("The Hobbit");
  }

  @Test
  @DisplayName("평점 기준 내림차순으로 책 목록 조회 성공")
  void findBooksWithCursor_success_SortByRatingDesc() {
    // given
    String sortField = "rating";
    String sortOrder = "desc";

    // when
    List<Book> books = bookRepository.findBooksWithCursor(null, sortField, sortOrder, null, null, 5);

    // then
    assertThat(books).hasSize(5);
    assertThat(books.get(0).getTitle()).isEqualTo("The Hobbit");
    assertThat(books.get(1).getTitle()).isEqualTo("1984");
    assertThat(books.get(2).getTitle()).isEqualTo("Harry Potter");
    assertThat(books.get(3).getTitle()).isEqualTo("Pride and Prejudice");
    assertThat(books.get(4).getTitle()).isEqualTo("The Great Gatsby");
  }

  @Test
  @DisplayName("리뷰 수 기준 내림차순으로 책 목록 조회 성공")
  void findBooksWithCursor_success_SortByReviewCountDesc() {
    // given
    String sortField = "reviewCount";
    String sortOrder = "desc";

    // when
    List<Book> books = bookRepository.findBooksWithCursor(null, sortField, sortOrder, null, null, 5);

    // then
    assertThat(books).hasSize(5);
    assertThat(books.get(0).getTitle()).isEqualTo("The Hobbit");
    assertThat(books.get(1).getTitle()).isEqualTo("1984");
    assertThat(books.get(2).getTitle()).isEqualTo("The Great Gatsby");
    assertThat(books.get(3).getTitle()).isEqualTo("Harry Potter");
    assertThat(books.get(4).getTitle()).isEqualTo("Pride and Prejudice");
  }

  @Test
  @DisplayName("작가 이름으로 책 검색 성공")
  void findBooksWithCursor_success_WithAuthorKeyword() {
    // given
    String keyword = "Tolkien";

    // when
    List<Book> books = bookRepository.findBooksWithCursor(keyword, "title", "asc", null, null, 5);

    // then
    assertThat(books).hasSize(1);
    assertThat(books.get(0).getTitle()).isEqualTo("The Hobbit");
  }

  @Test
  @DisplayName("ISBN으로 책 검색 성공")
  void findBooksWithCursor_success_WithIsbnKeyword() {
    // given
    String keyword = "12345";

    // when
    List<Book> books = bookRepository.findBooksWithCursor(keyword, "title", "asc", null, null, 5);

    // then
    assertThat(books).hasSize(1);
  }

  @Test
  @DisplayName("커서 기반 페이지네이션으로 첫 페이지 조회 성공")
  void findBooksWithCursor_success_FirstPage() {
    // given
    int pageSize = 2;

    // when
    List<Book> books = bookRepository.findBooksWithCursor(null, "title", "asc", null, null, pageSize);

    // then
    assertThat(books).hasSize(2);
    assertThat(books.get(0).getTitle()).isEqualTo("1984");
    assertThat(books.get(1).getTitle()).isEqualTo("Harry Potter");
  }

  @Test
  @DisplayName("커서 기반 페이지네이션으로 두 번째 페이지 조회 성공")
  void findBooksWithCursor_success_SecondPage() {
    // given
    String cursor = "Harry Potter";
    LocalDateTime after = book1.getCreatedAt();
    int pageSize = 2;

    // when
    List<Book> books = bookRepository.findBooksWithCursor(null, "title", "asc", cursor, after, pageSize);

    // then
    assertThat(books).hasSize(2);
    assertThat(books.get(0).getTitle()).isEqualTo("Pride and Prejudice");
    assertThat(books.get(1).getTitle()).isEqualTo("The Great Gatsby");
  }

  @Test
  @DisplayName("커서 기반 페이지네이션으로 마지막 페이지 조회 성공")
  void findBooksWithCursor_success_LastPage() {
    // given
    String cursor = "The Great Gatsby";
    LocalDateTime after = book5.getCreatedAt();
    int pageSize = 2;

    // when
    List<Book> books = bookRepository.findBooksWithCursor(null, "title", "asc", cursor, after, pageSize);

    // then
    assertThat(books).hasSize(1);
    assertThat(books.get(0).getTitle()).isEqualTo("The Hobbit");
  }

  @Test
  @DisplayName("키워드에 해당하는 책 개수 조회 성공")
  void countByKeyword_success() {
    // given
    String keyword = "Potter";

    // when
    long count = bookRepository.countByKeyword(keyword);

    // then
    assertThat(count).isEqualTo(1);
  }
}