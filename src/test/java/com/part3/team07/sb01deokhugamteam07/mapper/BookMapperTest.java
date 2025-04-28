package com.part3.team07.sb01deokhugamteam07.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.storage.Storage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookMapperTest {

  @Mock
  private Storage storage;

  @InjectMocks
  private BookMapper bookMapper;

  private UUID id;
  private String title;
  private String author;
  private String description;
  private String publisher;
  private LocalDate publishedDate;
  private String isbn;
  private String thumbnailUrl;
  private Book book;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    title = "title";
    author = "author";
    description = "description";
    publisher = "publisher";
    thumbnailUrl = "thumbnail.jpg";
    publishedDate = LocalDate.of(1618, 1, 1);

    book = new Book(title, author, description, publisher, publishedDate,
        isbn, thumbnailUrl, 0, 0);
    ReflectionTestUtils.setField(book, "id", id);
    ReflectionTestUtils.setField(book, "createdAt", LocalDateTime.now());
    ReflectionTestUtils.setField(book, "updatedAt", LocalDateTime.now());
  }

  @Nested
  @DisplayName("toDto")
  class ToDtoTest {
    @Test
    @DisplayName("Book에 thumbnailUrl이 있을 경우 DTO에 썸네일 URL 포함")
    void toDto_WithThumbnail() {
      // given
      when(book.getThumbnailUrl()).thenReturn("http://thumbnail.com/thumbnail.jpg");

      // When: toDto 메서드를 호출하면
      BookDto bookDto = bookMapper.toDto(book);

      // Then: 반환된 DTO의 썸네일 URL이 정확해야 한다
      assertThat(bookDto.id()).isEqualTo(id);
      assertThat(bookDto.title()).isEqualTo(title);
      assertThat(bookDto.thumbnailUrl()).isEqualTo("http://thumbnail.com/thumbnail.jpg");
    }

    @Test
    @DisplayName("Book에 thumbnailUrl이 없을 경우 DTO에 썸네일 URL null")
    void toDto_WithoutThumbnail() {
      // given
      ReflectionTestUtils.setField(book, "thumbnailUrl", null);

      // when
      BookDto bookDto = bookMapper.toDto(book);

      // then
      assertThat(bookDto.thumbnailUrl()).isNull();
    }

  }
}