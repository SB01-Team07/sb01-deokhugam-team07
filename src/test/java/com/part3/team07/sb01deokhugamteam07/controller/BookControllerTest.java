package com.part3.team07.sb01deokhugamteam07.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.POST;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.NaverBookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.response.CursorPageResponseBookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.response.CursorPageResponsePopularBookDto;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetailsService;
import com.part3.team07.sb01deokhugamteam07.service.BookService;
import com.part3.team07.sb01deokhugamteam07.service.DashboardService;
import com.part3.team07.sb01deokhugamteam07.service.OcrService;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

@WithMockUser
@WebMvcTest(BookController.class)
public class BookControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CustomUserDetailsService customUserDetailsService;

  @MockitoBean
  private BookService bookService;

  @MockitoBean
  private DashboardService dashboardService;

  @MockitoBean
  OcrService ocrService;

  private UUID id;
  private String title;
  private String author;
  private String description;
  private String publisher;
  private LocalDate publishedDate;
  private String isbn;
  private Book book1, book2;
  private BookDto bookDto1, bookDto2;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    title = "title";
    author = "author";
    description = "description";
    publisher = "publisher";
    publishedDate = LocalDate.of(1618, 1, 1);

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
    setField(book1, "id", UUID.randomUUID());
    setField(book1, "createdAt",
        LocalDateTime.of(2023, 1, 1, 0, 0));
    setField(book1, "updatedAt", LocalDateTime.now());

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
    setField(book2, "id", UUID.randomUUID());
    setField(book2, "createdAt",
        LocalDateTime.of(2023, 1, 2, 0, 0));
    setField(book2, "updatedAt", LocalDateTime.now());

    bookDto1 = createBookDto(book1);
    bookDto2 = createBookDto(book2);
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

  private BookDto createBookDto(Book book) {
    return new BookDto(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getDescription(),
        book.getPublisher(),
        book.getPublishDate(),
        book.getIsbn(),
        book.getThumbnailUrl(),
        book.getReviewCount(),
        book.getRating(),
        book.getCreatedAt(),
        book.getUpdatedAt()
    );
  }

  @Test
  @DisplayName("도서 생성")
  void create() throws Exception {
    // given
    BookCreateRequest request = new BookCreateRequest(
        title,
        author,
        description,
        publisher,
        publishedDate,
        ""
    );

    MockMultipartFile bookCreateRequestPart = new MockMultipartFile(
        "bookData",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile thumbnailImagePart = new MockMultipartFile(
        "thumbnailImage",
        "test.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "test-image".getBytes()
    );

    BookDto createdBook = new BookDto(
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

    given(bookService.create(any(BookCreateRequest.class), any(MultipartFile.class)))
        .willReturn(createdBook);

    // when & then
    mockMvc.perform(multipart("/api/books")
            .file(bookCreateRequestPart)
            .file(thumbnailImagePart)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .with(csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.title").value(title));
  }

  @Nested
  @DisplayName("도서 수정")
  class UpdateTest {
    @Test
    @DisplayName("도서 수정 성공")
    void update_success() throws Exception {
      // given
      String newTitle = "new title";
      String newAuthor = "new author";
      String newDescription = "new description";
      String newPublisher = null;
      LocalDate newPublishedDate = LocalDate.of(2025, 4, 22);

      BookUpdateRequest request = new BookUpdateRequest(
          newTitle,
          newAuthor,
          newDescription,
          newPublisher,
          newPublishedDate
      );

      MockMultipartFile bookUpdateRequestPart = new MockMultipartFile(
          "bookData",
          "",
          MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsBytes(request)
      );

      MockMultipartFile thumbnailImagePart = new MockMultipartFile(
          "thumbnailImage",
          "test.jpg",
          MediaType.IMAGE_JPEG_VALUE,
          "test-image".getBytes()
      );

      BookDto updatedBook = new BookDto(
          id,
          newTitle,
          newAuthor,
          newDescription,
          publisher,
          newPublishedDate,
          "",
          "",
          0,
          BigDecimal.ZERO,
          LocalDateTime.now(),
          LocalDateTime.now()
      );

      given(bookService.update(eq(id), any(BookUpdateRequest.class), any(MultipartFile.class)))
          .willReturn(updatedBook);

      // when & then
      mockMvc.perform(MockMvcRequestBuilders.multipart("/api/books/{id}", id)
              .file(bookUpdateRequestPart)
              .file(thumbnailImagePart)
              .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
              .with(csrf())
              .with(patchRequest -> {
                patchRequest.setMethod("PATCH");
                return patchRequest;
              }))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id.toString()))
          .andExpect(jsonPath("$.title").value(newTitle))
          .andExpect(jsonPath("$.author").value(newAuthor))
          .andExpect(jsonPath("$.description").value(newDescription))
          .andExpect(jsonPath("$.publisher").value(publisher))
          .andExpect(jsonPath("$.publishedDate").value("2025-04-22"));
    }

    @Test
    @DisplayName("도서 수정 실패 - 허용되지 않은 값")
    void update_fail_NullableNotBlank() throws Exception {
      // given
      String newTitle = "";
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

      MockMultipartFile bookUpdateRequestPart = new MockMultipartFile(
          "bookData",
          "",
          MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsBytes(request)
      );

      MockMultipartFile thumbnailImagePart = new MockMultipartFile(
          "thumbnailImage",
          "test.jpg",
          MediaType.IMAGE_JPEG_VALUE,
          "test-image".getBytes()
      );

      // when & then
      mockMvc.perform(MockMvcRequestBuilders.multipart("/api/books/{id}", id)
          .file(bookUpdateRequestPart)
          .file(thumbnailImagePart)
          .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
          .with(csrf())
          .with(patchRequest -> {
            patchRequest.setMethod("PATCH");
            return patchRequest;
          }))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("도서 논리 삭제")
  class SoftDeleteTest {
    @Test
    @DisplayName("도서 논리 삭제 성공")
    void softDelete_success() throws Exception {
      // given
      willDoNothing().given(bookService).softDelete(id);

      // when & then
      mockMvc.perform(delete("/api/books/{id}", id)
              .contentType(MediaType.APPLICATION_JSON)
              .with(csrf()))
          .andExpect(status().isNoContent());
    }
  }

  @Nested
  @DisplayName("도서 물리 삭제")
  class hardDeleteTest {
    @Test
    @DisplayName("도서 물리 삭제 성공")
    void hardDelete_success() throws Exception {
      // given
      willDoNothing().given(bookService).hardDelete(id);

      // when & then
      mockMvc.perform(delete("/api/books/{id}/hard", id)
              .contentType(MediaType.APPLICATION_JSON)
              .with(csrf()))
          .andExpect(status().isNoContent());
    }
  }

  @Nested
  @DisplayName("도서 상세 정보 조회")
  class FindTest {
    @Test
    @DisplayName("도서 상세 정보 조회 성공")
    void find_success() throws Exception {
      // given
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

      given(bookService.find(id)).willReturn(bookDto);

      // when & then
      mockMvc.perform(get("/api/books/{id}", id)
              .contentType(MediaType.APPLICATION_JSON)
              .with(csrf()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id.toString()))
          .andExpect(jsonPath("$.title").value("title"))
          .andExpect(jsonPath("$.author").value("author"))
          .andExpect(jsonPath("$.reviewCount").value(0))
          .andExpect(jsonPath("$.rating").value(0));
    }
  }

  @Nested
  @DisplayName("도서 목록 조회")
  class FindAllTest {
    @Test
    @DisplayName("도서 목록 조회 성공")
    void findAll_success() throws Exception {
      // given
      LocalDateTime nextAfter = LocalDateTime.of(2023, 1, 2, 0, 0);

      CursorPageResponseBookDto response = new CursorPageResponseBookDto(
          List.of(bookDto1, bookDto2),
          LocalDateTime.of(1937, 9, 21, 0, 0).toString(),
          nextAfter,
          2,
          10,
          true
      );

      given(bookService.findAll(
          anyString(),
          anyString(),
          anyString(),
          nullable(String.class),
          nullable(LocalDateTime.class),
          anyInt()
      )).willReturn(response);


      // when & then
      mockMvc.perform(get("/api/books")
              .contentType(MediaType.APPLICATION_JSON)
              .with(csrf())
              .param("keyword", "fantasy")
              .param("orderBy", "publishedDate")
              .param("direction", "desc")
              .param("size", "2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content", hasSize(2)))
          .andExpect(jsonPath("$.content[0].title", is("Harry Potter")))
          .andExpect(jsonPath("$.content[1].title", is("The Hobbit")))
          .andExpect(jsonPath("$.size", is(2)))
          .andExpect(jsonPath("$.totalElements", is(10)))
          .andExpect(jsonPath("$.hasNext", is(true)));
    }
  }

  @Test
  @DisplayName("인기 도서 조회")
  void findPopularBooks_success() throws Exception {
    Period period = Period.DAILY;
    String direction = "asc";
    String cursor = null;
    String after = null;
    int limit = 50;

    CursorPageResponsePopularBookDto cursorPageResponsePopularBookDto =
        CursorPageResponsePopularBookDto.builder()
            .hasNext(false)
            .build();

    when(dashboardService.getPopularBooks(period,direction,cursor,after,limit)).thenReturn(cursorPageResponsePopularBookDto);

    mockMvc.perform(get("/api/books/popular")
            .param("period", period.toString())
            .param("direction",direction)
            .param("cursor", cursor)
            .param("after", after)
            .param("limit", String.valueOf(limit))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hasNext" ).value(false));
  }

  @Nested
  @DisplayName("ISBN으로 도서 정보 조회")
  class GetInfoTest {
    @Test
    @DisplayName("getInfo 성공")
    void getInfo_success() throws Exception {
      // given
      String isbn = "9788960515529";
      NaverBookDto dto = new NaverBookDto(
          "자바의 정석",
          "남궁성",
          "자바를 완벽히 이해할 수 있는 책",
          "도우출판",
          LocalDate.of(2013, 1, 1),
          isbn,
          "http://example.com/image.jpg".getBytes()
      );

      given(bookService.getInfo(isbn)).willReturn(dto);

      // when & then
      mockMvc.perform(get("/api/books/info")
              .contentType(MediaType.APPLICATION_JSON)
              .with(csrf())
              .param("isbn", isbn))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.title").value("자바의 정석"))
          .andExpect(jsonPath("$.author").value("남궁성"))
          .andExpect(jsonPath("$.isbn").value(isbn));
    }
  }

  @Test
  void extractIsbnByOcr_success() throws Exception {
    // given
    String expectedIsbn = "9781234567890";
    MockMultipartFile mockImage = new MockMultipartFile("image",
        "test.jpg",
        MediaType.IMAGE_JPEG_VALUE, "dummy image content".getBytes());

    given(ocrService.extractIsbn13(mockImage)).willReturn(expectedIsbn);

    // when & then
    mockMvc.perform(MockMvcRequestBuilders
            .multipart("/api/books/isbn/ocr")
            .file(mockImage)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(expectedIsbn));
  }
}
