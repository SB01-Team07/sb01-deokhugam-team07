package com.part3.team07.sb01deokhugamteam07.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewLikeDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.review.response.CursorPageResponsePopularReviewDto;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.review.ReviewAlreadyExistsException;
import com.part3.team07.sb01deokhugamteam07.exception.review.ReviewNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.review.ReviewUnauthorizedException;
import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetailsService;
import com.part3.team07.sb01deokhugamteam07.service.DashboardService;
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
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @MockitoBean
    private DashboardService dashboardService;

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

    @DisplayName("리뷰 생성 - 서버 에러 발생 시 500 반환")
    @Test
    void createReview_InternalServerError() throws Exception {
        UUID bookId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReviewCreateRequest request = new ReviewCreateRequest(bookId, userId, "리뷰 내용", 5);

        given(reviewService.create(any())).willThrow(RuntimeException.class);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
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
        given(reviewService.find(reviewId, userId)).willReturn(reviewDto);

        //then
        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
                        .header("Deokhugam-Request-User-ID", userId.toString())
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


    @DisplayName("리뷰 상세 조회 - 존재하지 않는 리뷰일 경우 404 반환")
    @Test
    void findReview_Failure_ReviewNotFound() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(reviewService.find(reviewId, userId))
                .willThrow(new ReviewNotFoundException());

        // when & then
        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
                        .header("Deokhugam-Request-User-ID", userId.toString())
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

    @DisplayName("리뷰 삭제 - 요청자 ID 헤더 누락 시 400 반환")
    @Test
    void softDeleteReview_Failure_MissingUserIdHeader() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();

        //when then
        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_HEADER"))
                .andExpect(jsonPath("$.exceptionType").value("MissingRequestHeaderException"));
    }

    @DisplayName("리뷰 삭제 - 삭제 권한 없을 경우 403 반환")
    @Test
    void softDeleteReview_Failure_Unauthorized() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        willThrow(new ReviewUnauthorizedException()).given(reviewService).softDelete(userId, reviewId);

        //when then
        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                        .header("Deokhugam-Request-User-ID", userId.toString())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("REVIEW_UNAUTHORIZED"))
                .andExpect(jsonPath("$.exceptionType").value("ReviewUnauthorizedException"));
    }

    @DisplayName("리뷰 삭제 - 리뷰가 존재하지 않을 경우 404 반환")
    @Test
    void softDeleteReview_Failure_ReviewNotFound() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        willThrow(new ReviewNotFoundException()).given(reviewService).softDelete(userId, reviewId);

        // when then
        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                        .header("Deokhugam-Request-User-ID", userId.toString())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("REVIEW_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("ReviewNotFoundException"));
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

    @DisplayName("리뷰 물리 삭제 - 요청자 ID 헤더 누락 시 400 반환")
    @Test
    void hardDelete_Failure_MissingUserIdHeader() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();

        //when then
        mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_HEADER"))
                .andExpect(jsonPath("$.exceptionType").value("MissingRequestHeaderException"));
    }

    @DisplayName("리뷰 물리 삭제 - 삭제 권한 없을 경우 403 반환")
    @Test
    void hardDelete_Failure_Unauthorized() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        willThrow(new ReviewUnauthorizedException()) //삭제 리턴 값이 없어서 이렇게 해야하네
                .given(reviewService)
                .hardDelete(userId, reviewId);

        //when then
        mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId)
                        .header("Deokhugam-Request-User-ID", userId.toString())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("REVIEW_UNAUTHORIZED"))
                .andExpect(jsonPath("$.exceptionType").value("ReviewUnauthorizedException"));
    }

    @DisplayName("리뷰 물리 삭제 - 리뷰가 존재하지 않을 경우 404 반환")
    @Test
    void hardDelete_Failure_ReviewNotFound() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        willThrow(new ReviewNotFoundException())
                .given(reviewService)
                .hardDelete(userId, reviewId);

        // when then
        mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId)
                        .header("Deokhugam-Request-User-ID", userId.toString())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("REVIEW_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("ReviewNotFoundException"));
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

    @DisplayName("리뷰 좋아요 토글 - 요청자 ID 헤더 누락 시 400 반환")
    @Test
    void toggleLike_Failure_MissingUserIdHeader() throws Exception {
        //given
        UUID reviewId = UUID.randomUUID();

        //when then
        mockMvc.perform(post("/api/reviews/{reviewId}/like", reviewId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_HEADER"))
                .andExpect(jsonPath("$.exceptionType").value("MissingRequestHeaderException"));
    }

    @DisplayName("리뷰 좋아요 토글 - 리뷰가 존재하지 않을 경우 404 반환")
    @Test
    void toggleLike_Failure_ReviewNotFound() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        willThrow(new ReviewNotFoundException()).given(reviewService)
                .toggleLike(reviewId, userId);

        //when then
        mockMvc.perform(post("/api/reviews/{reviewId}/like", reviewId)
                        .header("Deokhugam-Request-User-ID", userId.toString())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("REVIEW_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("ReviewNotFoundException"));
    }

    @Test
    @DisplayName("인기 리뷰 조회")
    void findPopularReviews() throws Exception {
        Period period = Period.DAILY;
        String direction = "asc";
        String cursor = null;
        String after = null;
        int limit = 50;

        CursorPageResponsePopularReviewDto cursorPageResponsePopularReviewDto =
            CursorPageResponsePopularReviewDto.builder()
                .hasNext(false)
                .build();

        when(dashboardService.getPopularReviews(period,direction,cursor,after,limit)).thenReturn(cursorPageResponsePopularReviewDto);

        mockMvc.perform(get("/api/reviews/popular")
                .param("period", period.toString())
                .param("direction",direction)
                .param("cursor", cursor)
                .param("after", after)
                .param("limit", String.valueOf(limit))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasNext" ).value(false));
    }
}