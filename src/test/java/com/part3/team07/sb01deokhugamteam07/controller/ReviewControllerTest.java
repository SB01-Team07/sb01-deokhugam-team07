package com.part3.team07.sb01deokhugamteam07.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewLikeDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.review.response.CursorPageResponsePopularReviewDto;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
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


    // TODO: 커스텀 예외 추가시 변경 예정
/*    @DisplayName("존재하지 않은 리뷰는 상세 조회 불가능 하다.")
    @Test
    void findReview_Failure_NotFound() throws Exception {
        //given
        UUID invalidReviewId = UUID.randomUUID();
        given(reviewService.find(invalidReviewId))
                .willThrow(new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        //when then
        mockMvc.perform(get("/api/reviews/{reviewId}", invalidReviewId)
                    .contentType(MediaType.APPLICATION_JSON));
                //.andExpect(status().isBadRequest());
    }*/

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

    // TODO: 커스텀 예외 추가시 변경 예정
/*    @DisplayName("작성자가 아닌 사용자가 리뷰를 작성하는 경우 400 에러가 발생한다.")
    @Test
    void test() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        ReviewUpdateRequest request = new ReviewUpdateRequest("수정한 내용", 3);

        //when
        given(reviewService.update(userId, reviewId, request))
                .willThrow(new IllegalArgumentException("본인이 작성한 리뷰가 아닙니다."));

        //then
        mockMvc.perform(patch("/api/reviews/{reviewId}", reviewId)
                .header("Deokhugam-Request-User-ID", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }*/

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