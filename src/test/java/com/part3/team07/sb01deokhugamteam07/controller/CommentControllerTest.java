package com.part3.team07.sb01deokhugamteam07.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.service.CommentService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false) // todo: 배포 전 삭제 예정
class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CommentService commentService;

  @Test
  @DisplayName("댓글 생성 성공")
  void createComment() throws Exception{
    //given
    UUID testReviewId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();
    UUID testCommentId = UUID.randomUUID();
    LocalDateTime fixedNow = LocalDateTime.now();
    String content = "test";

    CommentCreateRequest createRequest = new CommentCreateRequest(
        testReviewId,
        testUserId,
        content
    );

    CommentDto createdComment = new CommentDto(
        testCommentId,
        testReviewId,
        testUserId,
        "test",
        content,
        fixedNow,
        fixedNow
    );

    given(commentService.create(any(CommentCreateRequest.class)))
        .willReturn(createdComment);

    // when & then
    mockMvc.perform(post("/api/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(testCommentId.toString()))
        .andExpect(jsonPath("$.content").value(content))
        .andExpect(jsonPath("$.userId").value(testUserId.toString()))
        .andExpect(jsonPath("$.reviewId").value(testReviewId.toString()));
  }

  @Test
  @DisplayName("댓글 생성 실패 - 잘못된 요청")
  void createCommentFailByInvalidRequest() throws Exception {
    // given
    CommentCreateRequest invalidRequest = new CommentCreateRequest(
        null,
        null,
        ""
    );

    // when & then
    mockMvc.perform(post("/api/comments")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("댓글 수정 성공")
  void updateComment() throws Exception{
    //given
    String newContent = "updated content";
    UUID testCommentId = UUID.randomUUID();
    UUID testReviewId = UUID.randomUUID();
    UUID testUserId = UUID.randomUUID();
    LocalDateTime fixedNow = LocalDateTime.now();

    CommentDto updatedComment = new CommentDto(
        testCommentId,
        testReviewId,
        testUserId,
        "userNickname",
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
        .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testCommentId.toString()))
        .andExpect(jsonPath("$.content").value(newContent))
        .andExpect(jsonPath("$.userId").value(testUserId.toString()));
  }

  @Test
  @DisplayName("댓글 수정 실패 - 잘못된 요청")
  void updateCommentFailByInvalidRequest() throws Exception{
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
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }
}