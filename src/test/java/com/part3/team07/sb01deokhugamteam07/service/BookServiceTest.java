package com.part3.team07.sb01deokhugamteam07.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookCreateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.mapper.BookMapper;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock
  private BookRepository bookRepository;

  @Mock
  private BookMapper bookMapper;

  @InjectMocks
  private BookService bookService;

  private UUID id;
  private String title;
  private String author;
  private String description;
  private String publisher;
  private LocalDate publishedDate;
  private String isbn;
  private Book book;
  private BookDto bookDto;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    title = "홍길동전";
    author = "허균";
    description = "고전 소설";
    publisher = "출판사";
    publishedDate = LocalDate.of(1618, 1, 1);

    book = new Book(title, author, description, publisher, publishedDate,
        isbn, "", 0, 0);

    bookDto = new BookDto(
        id,
        title,
        author,
        description,
        publisher,
        publishedDate,
        "",
        "",
        0,
        0,
        LocalDateTime.now(),
        LocalDateTime.now()
    );
  }

  @Test
  @DisplayName("도서 생성 성공")
  void create() {
    // given
    BookCreateRequest request = new BookCreateRequest(
        title,
        author,
        description,
        publisher,
        publishedDate,
        ""
    );

    given(bookRepository.save(any(Book.class))).will(invocation -> {
      Book book = invocation.getArgument(0);
      ReflectionTestUtils.setField(book, "id", id);
      return book;
    });
    given(bookMapper.toDto(any(Book.class))).willReturn(bookDto);

    // when
    BookDto result = bookService.create(request);

    // then
    assertThat(result).isEqualTo(bookDto);
    verify(bookRepository).save(any(Book.class));
  }

}