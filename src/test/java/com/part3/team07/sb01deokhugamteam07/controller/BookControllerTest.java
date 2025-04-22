package com.part3.team07.sb01deokhugamteam07.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.book.request.BookUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetailsService;
import com.part3.team07.sb01deokhugamteam07.service.BookService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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

  private UUID id;
  private String title;
  private String author;
  private String description;
  private String publisher;
  private LocalDate publishedDate;
  private String isbn;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    title = "title";
    author = "author";
    description = "description";
    publisher = "publisher";
    publishedDate = LocalDate.of(1618, 1, 1);
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
        0,
        LocalDateTime.now(),
        LocalDateTime.now()
    );

    given(bookService.create(any(BookCreateRequest.class), any(MultipartFile.class)))
        .willReturn(createdBook);

    // when & then
    mockMvc.perform(multipart("/api/books")
            .file(bookCreateRequestPart)
            .file(thumbnailImagePart)
            .content(MediaType.MULTIPART_FORM_DATA_VALUE)
            .with(csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.title").value(title));
  }

  @Test
  @DisplayName("도서 수정")
  void update() throws Exception {
    // given
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

    BookDto updatedBook = new BookDto(
        id,
        newTitle,
        newAuthor,
        newDescription,
        newPublisher,
        newPublishedDate,
        "",
        "",
        0,
        0,
        LocalDateTime.now(),
        LocalDateTime.now()
    );

    given(bookService.update(eq(id), any(BookUpdateRequest.class)))
        .willReturn(updatedBook);

    // when & then
    mockMvc.perform(patch("/api/books/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.title").value(newTitle))
        .andExpect(jsonPath("$.author").value(newAuthor))
        .andExpect(jsonPath("$.description").value(newDescription))
        .andExpect(jsonPath("$.publisher").value(newPublisher))
        .andExpect(jsonPath("$.publishedDate").value("2025-04-22"));
  }
}
