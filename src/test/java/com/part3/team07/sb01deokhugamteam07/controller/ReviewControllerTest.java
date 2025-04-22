package com.part3.team07.sb01deokhugamteam07.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetailsService;
import com.part3.team07.sb01deokhugamteam07.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@AutoConfigureMockMvc(addFilters = false) 필터(보안 검사) 꺼버리기
@WithMockUser
@WebMvcTest(ReviewController.class)
class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService; //시큐리티를 위한 목 객체 생성

    @DisplayName("리뷰를 생성할 수 있다.")
    @Test
    void createReview_Success() throws Exception {
        //given
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ReviewCreateRequest createRequest = new ReviewCreateRequest(bookId, userId, "책입니다", 5
        );

        ReviewDto reviewDto = new ReviewDto(
                reviewId,
                bookId,
                "Book",
                "url",
                userId,
                "User",
                "책입니다",
                5,
                0,
                0,
                false,
                now,
                now
        );

        given(reviewService.create(any(ReviewCreateRequest.class)))
                .willReturn(reviewDto);

        //when then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .with(csrf())) //csrf 추가
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.bookId").value(bookId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.bookTitle").value("Book"))
                .andExpect(jsonPath("$.bookThumbnailUrl").value("url"))
                .andExpect(jsonPath("$.userNickName").value("User"))
                .andExpect(jsonPath("$.content").value("책입니다"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.commentCount").value(0))
                .andExpect(jsonPath("$.likeByMe").value(false))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("유효하지 않은 입력이 들어오면 리뷰를 생성할 수 없다.")
    void createReview_Failure_InvalidRequest() throws Exception {
        // Given
        ReviewCreateRequest invalidRequest = new ReviewCreateRequest(null, null, "", 6);

        // When & Then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf())) //csrf 추가
                .andExpect(status().isBadRequest());
    }

    @DisplayName("리뷰 상세 조회를 할 수 있다.")
    @Test
    void find() throws Exception {
        //given
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ReviewDto reviewDto = new ReviewDto(
                reviewId,
                bookId,
                "Book",
                "url",
                userId,
                "User",
                "책입니다",
                5,
                0,
                0,
                false,
                now,
                now
        );

        //when
        given(reviewService.find(reviewId)).willReturn(reviewDto);

        //then
        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId.toString()));

    }
}