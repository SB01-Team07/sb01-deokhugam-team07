package com.part3.team07.sb01deokhugamteam07.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewLikeDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.review.ReviewAlreadyExistsException;
import com.part3.team07.sb01deokhugamteam07.exception.review.ReviewNotFoundException;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @DisplayName("리뷰 생성 - 입력값 검증 실패 시 400 반환")
    void createReview_Failure_InvalidRequest() throws Exception {
        //given
        ReviewCreateRequest invalidRequest = new ReviewCreateRequest(null, null, "", 6);

        // when then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"));
    }

    @DisplayName("리뷰 생성 - 존재하지 않은 책일 경우 404 반환")
    @Test
    void createReview_ShouldReturn404_WhenBookNotFound() throws Exception {
        //given
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReviewCreateRequest request = new ReviewCreateRequest(bookId, userId, "리뷰 내용", 5);

        given(reviewService.create(any())).willThrow(new BookNotFoundException());

        //when then
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("BookNotFoundException"));
    }

    @DisplayName("리뷰 생성 - 이미 작성된 리뷰가 존재할 경우 409 반환")
    @Test
    void createReview_Failure_DuplicateReview() throws Exception {
        // given
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReviewCreateRequest request = new ReviewCreateRequest(bookId, userId, "리뷰 내용", 5);

        given(reviewService.create(any())).willThrow(new ReviewAlreadyExistsException());

        // when then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isConflict()) // 409
                .andExpect(jsonPath("$.code").value("DUPLICATE_REVIEW"))
                .andExpect(jsonPath("$.exceptionType").value("ReviewAlreadyExistsException"));
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

    // TODO 리뷰 상세 조회 리펙터링 이후 리펙터링
/*
    @DisplayName("리뷰 상세 조회 - 요청자 ID 헤더 누락 시 400 반환")
    @Test
    void findReview_Failure_MissingUserIdHeader() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();

        //when then
        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_HEADER"))
                .andExpect(jsonPath("$.exceptionType").value("MissingRequestHeaderException"));
    }
*/


    @DisplayName("리뷰 상세 조회 - 존재하지 않는 리뷰일 경우 404 반환")
    @Test
    void findReview_Failure_ReviewNotFound() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();
        given(reviewService.find(reviewId))
                .willThrow(new ReviewNotFoundException());

        // when & then
        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("REVIEW_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("ReviewNotFoundException"));

    }

    @DisplayName("리뷰를 수정할 수 있다.")
    @Test
    void update() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        ReviewUpdateRequest request = new ReviewUpdateRequest("변경 내용",3);
        ReviewDto reviewDto = new ReviewDto(
                reviewId,
                bookId,
                "Book",
                "url",
                userId,
                "User",
                "변경 내용",
                3,
                0,
                0,
                false,
                now,
                now
        );

        given(reviewService.update(userId, reviewId, request)).willReturn(reviewDto);

        //when then
        mockMvc.perform(patch("/api/reviews/{reviewId}",reviewId)
                    .header("Deokhugam-Request-User-ID", userId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(reviewDto.content()))
                .andExpect(jsonPath("$.rating").value(reviewDto.rating()));
    }

    @DisplayName("리뷰 수정 - 요청자 ID 헤더 누락 시 400 반환")
    @Test
    void updateReview_Failure_MissingUserIdHeader() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();
        ReviewUpdateRequest request = new ReviewUpdateRequest("변경 내용", 3);

        //when then
        mockMvc.perform(patch("/api/reviews/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_HEADER"))
                .andExpect(jsonPath("$.exceptionType").value("MissingRequestHeaderException"));
    }

    @DisplayName("리뷰 수정 - 리뷰가 존재하지 않을 경우 404 반환")
    @Test
    void updateReview_Failure_ReviewNotFound() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReviewUpdateRequest request = new ReviewUpdateRequest("변경 내용", 3);

        given(reviewService.update(userId, reviewId, request))
                .willThrow(new ReviewNotFoundException());

        //when then
        mockMvc.perform(patch("/api/reviews/{reviewId}", reviewId)
                        .header("Deokhugam-Request-User-ID", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("REVIEW_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("ReviewNotFoundException"));
    }

    @DisplayName("리뷰를 논리 삭제할 수 있다.")
    @Test
    void softDelete() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        //when
        willDoNothing().given(reviewService).softDelete(userId, reviewId);

        //then
        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                .header("Deokhugam-Request-User-ID", userId.toString())
                .with(csrf()))
                    .andExpect(status().isNoContent());
        verify(reviewService).softDelete(userId, reviewId);
    }





    @DisplayName("리뷰를 물리 삭제할 수 있다.")
    @Test
    void hardDelete() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        //when
        willDoNothing().given(reviewService).hardDelete(userId, reviewId);

        //then
        mockMvc.perform(delete("/api/reviews/{reviewId}/hard",reviewId)
                .header("Deokhugam-Request-User-ID", userId.toString())
                .with(csrf()))
                .andExpect(status().isNoContent());
        verify(reviewService).hardDelete(userId, reviewId);
    }

    @DisplayName("리뷰에 좋아요 등록, 취소를 할 수 있다.")
    @Test
    void toggleLike() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        ReviewLikeDto reviewLikeDto = new ReviewLikeDto(reviewId, userId, true);

        given(reviewService.toggleLike(reviewId, userId)).willReturn(reviewLikeDto);

        //when then
        mockMvc.perform(post("/api/reviews/{reviewId}/like",reviewId)
                .header("Deokhugam-Request-User-ID", userId.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(reviewId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    // TODO: 커스텀 예외 추가시 변경 예정
/*    @Test
    @DisplayName("존재하지 않는 리뷰 ID일 경우 404 예외 발생")
    void toggleLike_reviewNotFound_shouldReturn404() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        given(reviewService.toggleLike(reviewId, userId))
                .willThrow(new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(post("/api/reviews/{reviewId}/like", reviewId)
                        .header("Deokhugam-Request-User-ID", userId))
                .andExpect(status().isNotFound()); //404 에러
    }*/

}