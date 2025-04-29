package com.part3.team07.sb01deokhugamteam07.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.FileType;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookAlreadyExistsException;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookNotFoundException;
import com.part3.team07.sb01deokhugamteam07.mapper.BookMapper;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock
  private BookRepository bookRepository;

  @Mock
  private BookMapper bookMapper;

  @Mock
  private StorageService storageService;

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

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    title = "title";
    author = "author";
    description = "description";
    publisher = "publisher";
    publishedDate = LocalDate.of(1618, 1, 1);

    book = new Book(title, author, description, publisher, publishedDate,
        isbn, "", 0, BigDecimal.ZERO);
  }

  @Nested
  @DisplayName("도서 생성")
  class CreateTest {
    @Test
    @DisplayName("도서 생성 성공")
    void create_success() {
      // given
      BookCreateRequest request = new BookCreateRequest(
          title,
          author,
          description,
          publisher,
          publishedDate,
          ""
      );

      BookDto bookDto = new BookDto(
          id,
          title,
          author,
          description,
          publisher,
          publishedDate,
          "",
          "",
          0,
          BigDecimal.ZERO,
          LocalDateTime.now(),
          LocalDateTime.now()
      );

      given(bookRepository.existsByIsbn(request.isbn())).willReturn(false);
      given(bookRepository.save(any(Book.class))).will(invocation -> {
        Book book = invocation.getArgument(0);
        ReflectionTestUtils.setField(book, "id", id);
        return book;
      });
      given(bookMapper.toDto(any(Book.class))).willReturn(bookDto);

      // when
      BookDto result = bookService.create(request, null);

      // then
      assertThat(result).isEqualTo(bookDto);
      verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("도서 생성 실패 - 중복 ISBN")
    void create_fail_duplicateIsbn() {
      // given
      BookCreateRequest request = new BookCreateRequest(
          title,
          author,
          description,
          publisher,
          publishedDate,
          ""
      );

      given(bookRepository.existsByIsbn(request.isbn())).willReturn(true);

      // when & then
      assertThrows(BookAlreadyExistsException.class,
          () -> bookService.create(request, null)
      );
    }
  }

  @Nested
  @DisplayName("도서 수정")
  class UpdateTest {
    @Test
    @DisplayName("도서 수정 성공")
    void update_success() {
      // given
      String newTitle = "new title";
      String newAuthor = "new author";
      String newDescription = "new description";
      String newPublisher = "new publisher";
      String newThumbnailUrl = "new-thumbnail.png";
      LocalDate newPublishedDate = LocalDate.of(2025, 4, 22);

      BookUpdateRequest request = new BookUpdateRequest(
          newTitle,
          newAuthor,
          newDescription,
          newPublisher,
          newPublishedDate
      );

      MultipartFile thumbnailImage = new MockMultipartFile(
          "thumbnail", "new-thumbnail.png", "image/png", "dummy".getBytes()
      );

      BookDto newBookDto = new BookDto(
          id,
          newTitle,
          newAuthor,
          newDescription,
          newPublisher,
          newPublishedDate,
          "",
          newThumbnailUrl,
          0,
          BigDecimal.ZERO,
          LocalDateTime.now(),
          LocalDateTime.now()
      );

      given(bookRepository.findById(id)).willReturn(Optional.of(book));
      given(storageService.save(thumbnailImage, FileType.THUMBNAIL_IMAGE)).willReturn(newThumbnailUrl);
      given(bookMapper.toDto(any(Book.class))).willReturn(newBookDto);

      // when
      BookDto result = bookService.update(id, request, thumbnailImage);

      // then
      assertThat(result.title()).isEqualTo(newTitle);
      assertThat(result.author()).isEqualTo(newAuthor);
      assertThat(result.description()).isEqualTo(newDescription);
      assertThat(result.publisher()).isEqualTo(newPublisher);
      assertThat(result.publishedDate()).isEqualTo(newPublishedDate);
      assertThat(result.thumbnailUrl()).isEqualTo(newThumbnailUrl);
    }
  }

  @Test
  @DisplayName("도서 수정 실패 - 없는 id")
  void update_fail_idNotFound() {
    // given
    UUID nonExistentId = UUID.randomUUID();
    String newTitle = "new title";
    String newAuthor = "new author";
    String newDescription = "new description";
    String newPublisher = "new publisher";
    LocalDate newPublishedDate = LocalDate.of(2025, 4, 22);

    BookUpdateRequest request = new BookUpdateRequest(
        newTitle,
        newAuthor,
        newDescription,
        newPublisher,
        newPublishedDate
    );

    given(bookRepository.findById(nonExistentId)).willReturn(Optional.empty());

    // when & then
    assertThrows(BookNotFoundException.class,
        () -> bookService.update(nonExistentId, request, null)
    );
  }

  @Nested
  @DisplayName("도서 논리 삭제")
  class SoftDeleteTest {
    @Test
    @DisplayName("도서 논리 삭제 성공")
    void softDelete_success() {
      // given
      Book spyBook = spy(book);
      given(bookRepository.findById(id)).willReturn(Optional.of(spyBook));

      // when
      bookService.softDelete(id);

      // then
      verify(spyBook).softDelete();
      assertTrue(spyBook.isDeleted());
    }

    @Test
    @DisplayName("도서 논리 삭제 실패 - 없는 id")
    void softDelete_fail_idNotFound() {
      // given
      given(bookRepository.findById(id)).willReturn(Optional.empty());

      // when & then
      assertThrows(BookNotFoundException.class,
          () -> bookService.softDelete(id)
      );
    }
  }

  @Nested
  @DisplayName("도서 물리 삭제")
  class HardDeleteTest {
    @Test
    @DisplayName("도서 물리 삭제 성공")
    void hardDelete_success() {
      // given
      given(bookRepository.existsById(id)).willReturn(true);

      // when
      bookService.hardDelete(id);

      // then
      verify(bookRepository).deleteById(id);
    }

    @Test
    @DisplayName("도서 물리 삭제 실패 - 없는 id")
    void hardDelete_fail_idNotFound() {
      // given
      given(bookRepository.existsById(id)).willReturn(false);

      // when & then
      assertThrows(BookNotFoundException.class,
          () -> bookService.hardDelete(id)
      );
    }
  }
}