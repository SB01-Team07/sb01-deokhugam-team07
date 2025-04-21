package com.part3.team07.sb01deokhugamteam07.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
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
@AutoConfigureMockMvc(addFilters = false) // 배포 전 삭제 예정
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

    CommentCreateRequest createRequest = new CommentCreateRequest(
        testReviewId,
        testUserId,
        "test"
    );

    CommentDto createdComment = new CommentDto(
        testCommentId,
        testReviewId,
        testUserId,
        "test",
        "test",
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
        .andExpect(jsonPath("$.content").value("test"))
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


}