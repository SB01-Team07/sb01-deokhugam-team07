package com.part3.team07.sb01deokhugamteam07.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.response.CursorPageResponseCommentDto;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.comment.CommentNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.comment.CommentUnauthorizedException;
import com.part3.team07.sb01deokhugamteam07.exception.comment.InvalidCommentQueryException;
import com.part3.team07.sb01deokhugamteam07.exception.review.ReviewNotFoundException;
import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetailsService;
import com.part3.team07.sb01deokhugamteam07.service.CommentService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CommentService commentService;

  @MockitoBean
  private CustomUserDetailsService customUserDetailsService;

  @Test
  @DisplayName("댓글 생성 성공")
  @WithMockUser
  void createComment() throws Exception {
    //given
    String content = "test";
    String userNickname = "userNickname";
    UUID testReviewId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();
    UUID testCommentId = UUID.randomUUID();
    LocalDateTime fixedNow = LocalDateTime.now();

    CommentCreateRequest createRequest = new CommentCreateRequest(
        testReviewId,
        testUserId,
        content
    );

    CommentDto createdComment = new CommentDto(
        testCommentId,
        testReviewId,
        testUserId,
        userNickname,
        content,
        fixedNow,
        fixedNow
    );

    given(commentService.create(any(CommentCreateRequest.class)))
        .willReturn(createdComment);

    // when & then
    mockMvc.perform(post("/api/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest))
            .with(csrf()))  // 스프링 시큐리티 토큰
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(testCommentId.toString()))
        .andExpect(jsonPath("$.content").value(content))
        .andExpect(jsonPath("$.userId").value(testUserId.toString()))
        .andExpect(jsonPath("$.reviewId").value(testReviewId.toString()))
        .andExpect(jsonPath("$.userNickname").value(userNickname));
  }

  @Test
  @DisplayName("댓글 생성 실패 - 400 잘못된 요청")
  @WithMockUser
  void createCommentFail_InvalidRequest() throws Exception {
    // given
    CommentCreateRequest invalidRequest = new CommentCreateRequest(
        null,
        null,
        ""
    );

    // when & then
    mockMvc.perform(post("/api/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest))
            .with(csrf()))  // 스프링 시큐리티 토큰
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("댓글 생성 실패 - 404 리뷰 존재X")
  @WithMockUser
  void createCommentFail_ReviewNotFound() throws Exception {
    // given
    UUID testReviewId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();
    CommentCreateRequest createRequest = new CommentCreateRequest(
        testReviewId,
        testUserId,
        "create content"
    );

    given(commentService.create(eq(createRequest))).willThrow(new ReviewNotFoundException());

    // when & then
    mockMvc.perform(post("/api/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest))
            .with(csrf()))  // 스프링 시큐리티 토큰
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("댓글 수정 성공")
  @WithMockUser
  void updateComment() throws Exception {
    //given
    String newContent = "updated content";
    String userNickname = "userNickname";
    UUID testCommentId = UUID.randomUUID();
    UUID testReviewId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();
    LocalDateTime fixedNow = LocalDateTime.now();

    CommentDto updatedComment = new CommentDto(
        testCommentId,
        testReviewId,
        testUserId,
        userNickname,
        newContent,
        fixedNow,
        fixedNow
    );

    CommentUpdateRequest updateRequest = new CommentUpdateRequest(
        newContent
    );

    given(commentService.update(any(UUID.class), any(UUID.class), any(CommentUpdateRequest.class)))
        .willReturn(updatedComment);

    //when & then
    mockMvc.perform(patch("/api/comments/{commentId}", testCommentId)
            .header("Deokhugam-Request-User-ID", testUserId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest))
            .with(csrf()))  // 스프링 시큐리티 토큰
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testCommentId.toString()))
        .andExpect(jsonPath("$.content").value(newContent))
        .andExpect(jsonPath("$.userId").value(testUserId.toString()));
  }

  @Test
  @DisplayName("댓글 수정 실패 - 400 잘못된 요청")
  @WithMockUser
  void updateCommentFail_InvalidRequest() throws Exception {
    //given
    UUID testCommentId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();
    CommentUpdateRequest invalidRequest = new CommentUpdateRequest(
        ""
    );

    //when & then
    mockMvc.perform(patch("/api/comments/{commentId}", testCommentId)
            .header("Deokhugam-Request-User-ID", testUserId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest))
            .with(csrf()))  // 스프링 시큐리티 토큰
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("댓글 수정 실패 - 403 권한 없음")
  @WithMockUser
  void updateCommentFail_ValidateCommentAuthor() throws Exception {
    //given
    UUID testCommentId = UUID.randomUUID();
    UUID notAuthorUserId = UUID.randomUUID();
    CommentUpdateRequest updateRequest = new CommentUpdateRequest(
        "new content"
    );

    given(commentService.update(eq(testCommentId), eq(notAuthorUserId),
        any(CommentUpdateRequest.class)))
        .willThrow(new CommentUnauthorizedException());
    // when & then
    mockMvc.perform(patch("/api/comments/{commentId}", testCommentId)
            .header("Deokhugam-Request-User-ID", notAuthorUserId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest))
            .with(csrf()))  // 스프링 시큐리티 토큰
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("댓글 수정 실패 - 404 리뷰 존재X")
  @WithMockUser
  void updateCommentFail_ReviewNotFound() throws Exception {
    // given
    UUID testCommentId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();
    CommentUpdateRequest updateRequest = new CommentUpdateRequest(
        "new content"
    );

    given(commentService.update(eq(testCommentId), eq(testUserId), any(CommentUpdateRequest.class)))
        .willThrow(new ReviewNotFoundException());

    // when & then
    mockMvc.perform(patch("/api/comments/{commentId}", testCommentId)
            .header("Deokhugam-Request-User-ID", testUserId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest))
            .with(csrf()))  // 스프링 시큐리티 토큰
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("댓글 상세 조회 성공")
  @WithMockUser
  void findComment() throws Exception {
    //given
    String content = "find test";
    String userNickname = "userNickname";
    UUID testReviewId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();
    UUID testCommentId = UUID.randomUUID();
    LocalDateTime fixedNow = LocalDateTime.now();

    CommentDto findComment = new CommentDto(
        testCommentId,
        testReviewId,
        testUserId,
        userNickname,
        content,
        fixedNow,
        fixedNow
    );

    given(commentService.find(any(UUID.class)))
        .willReturn(findComment);

    //when & then
    mockMvc.perform(get("/api/comments/{commentId}", testCommentId)
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())) // 스프링 시큐리티 토큰
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testCommentId.toString()))
        .andExpect(jsonPath("$.content").value(content))
        .andExpect(jsonPath("$.userId").value(testUserId.toString()))
        .andExpect(jsonPath("$.reviewId").value(testReviewId.toString()))
        .andExpect(jsonPath("$.userNickname").value(userNickname));
  }

  @Test
  @DisplayName("댓글 상세 조회 실패 - 404 댓글 존재X")
  @WithMockUser
  void findCommentFail_ReviewNotFound() throws Exception {
    //given
    UUID testCommentId = UUID.randomUUID();

    given(commentService.find(eq(testCommentId))).willThrow(new CommentNotFoundException());

    //when & then
    mockMvc.perform(get("/api/comments/{commentId}", testCommentId)
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())) // 스프링 시큐리티 토큰
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("댓글 논리 삭제 성공")
  @WithMockUser
  void softDeleteComment() throws Exception {
    //given
    UUID testUserId = UUID.randomUUID();
    UUID testCommentId = UUID.randomUUID();

    //when & then
    mockMvc.perform(delete("/api/comments/{commentId}", testCommentId)
            .header("Deokhugam-Request-User-ID", testUserId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())) // 스프링 시큐리티 토큰
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("댓글 물리 삭제 성공")
  @WithMockUser
  void hardDeleteComment() throws Exception {
    //given
    UUID testUserId = UUID.randomUUID();
    UUID testCommentId = UUID.randomUUID();

    //when & then
    mockMvc.perform(delete("/api/comments/{commentId}/hard", testCommentId)
            .header("Deokhugam-Request-User-ID", testUserId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())) // 스프링 시큐리티 토큰
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("댓글 목록 조회 성공")
  @WithMockUser
  void findCommentsByReviewId() throws Exception {
    //given
    UUID reviewId = UUID.randomUUID();

    CommentDto dto1 = new CommentDto(
        UUID.randomUUID(), reviewId, UUID.randomUUID(), "user1", "내용1",
        LocalDateTime.now(), LocalDateTime.now());

    CommentDto dto2 = new CommentDto(
        UUID.randomUUID(), reviewId, UUID.randomUUID(), "user2", "내용2",
        LocalDateTime.now(), LocalDateTime.now());

    List<CommentDto> content = List.of(dto1, dto2);

    CursorPageResponseCommentDto responseCommentDto = new CursorPageResponseCommentDto(
        content,
        LocalDateTime.now().toString(),
        LocalDateTime.now(),
        2,
        2,
        false
    );

    given(commentService.findCommentsByReviewId(
        eq(reviewId),
        eq("DESC"),
        isNull(),
        isNull(),
        eq(50)
    )).willReturn(responseCommentDto);

    //when & then
    mockMvc.perform(get("/api/comments")
            .param("reviewId", reviewId.toString())
            .param("direction", "DESC")
            .param("limit", "50")
            .with(csrf())) // 스프링 시큐리티 토큰
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.hasNext").value(false))
        .andExpect(jsonPath("$.size").value(2));
  }

  @Test
  @DisplayName("댓글 목록 조회 실패 - 404 리뷰 존재X")
  @WithMockUser
  void findCommentsByReviewIdFail_ReviewNotFound() throws Exception {
    //given
    UUID reviewId = UUID.randomUUID();
    given(commentService.findCommentsByReviewId(any(), any(), any(), any(), anyInt()))
        .willThrow(new ReviewNotFoundException());
    //when & then
    mockMvc.perform(get("/api/comments")
            .param("reviewId", reviewId.toString())
            .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("댓글 목록 조회 실패 - 400 잘못된 정렬")
  @WithMockUser
  void findCommentsByReviewIdFail_InvalidDirection() throws Exception {
    UUID reviewId = UUID.randomUUID();

    given(commentService.findCommentsByReviewId(any(), any(), any(), any(), anyInt()))
        .willThrow(InvalidCommentQueryException.direction());

    mockMvc.perform(get("/api/comments")
            .param("reviewId", reviewId.toString())
            .param("direction", "INVALID")
            .param("cursor", "")
            .param("after", "")
            .param("limit", "10")
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("댓글 목록 조회 실패 - 400 잘못된 커서")
  @WithMockUser
  void findCommentsByReviewIdFail_InvalidCursor() throws Exception {
    UUID reviewId = UUID.randomUUID();

    given(commentService.findCommentsByReviewId(any(), any(), any(), any(), anyInt()))
        .willThrow(InvalidCommentQueryException.cursor());

    mockMvc.perform(get("/api/comments")
            .param("reviewId", reviewId.toString())
            .param("direction", "DESC")
            .param("cursor", "InvalidCursor")
            .param("after", "")
            .param("limit", "10")
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("댓글 목록 조회 실패 - 400 reviewId 누락")
  @WithMockUser
  void findCommentsByReviewIdFail_ReviewIdMissing() throws Exception {
    mockMvc.perform(get("/api/comments")
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

}