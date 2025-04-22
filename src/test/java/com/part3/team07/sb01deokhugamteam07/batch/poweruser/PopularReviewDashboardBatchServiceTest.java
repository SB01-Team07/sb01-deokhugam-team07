package com.part3.team07.sb01deokhugamteam07.batch.poweruser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.entity.ValueType;
import com.part3.team07.sb01deokhugamteam07.repository.CommentRepository;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepository;
import com.part3.team07.sb01deokhugamteam07.repository.LikeRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PopularReviewDashboardBatchServiceTest {

  @Mock
  private DateRangeUtil dataRangeUtil;
  @Mock
  private AssignRankUtil assignReviewRank;
  @Mock
  private ReviewRepository reviewRepository;
  @Mock
  private CommentRepository commentRepository;
  @Mock
  private LikeRepository likeRepository;
  @Mock
  private DashboardRepository dashboardRepository;

  @InjectMocks
  private PopularReviewDashboardBatchService popularReviewDashboardBatchService;

  @Test
  @DisplayName("인기 리뷰 대시보드 데이터 저장 성공")
  void saveopularReviewDashboardData_success() {
    Period period = Period.WEEKLY;

    // Period 설정
    LocalDate[] dateRange = new LocalDate[]{
        LocalDate.of(2025, 4, 15),
        LocalDate.of(2025, 4, 21),
    };

    // 리뷰 설정
    UUID reviewId1 = UUID.randomUUID();
    UUID reviewId2 = UUID.randomUUID();

    Book book = Book.builder()
        .title("testBook")
        .thumbnailUrl("dummyUrl")
        .build();
    UUID bookId = UUID.randomUUID();
    ReflectionTestUtils.setField(book, "id", bookId);

    User user1 = User.builder()
        .nickname("testUser")
        .build();
    UUID userId1 = UUID.randomUUID();
    ReflectionTestUtils.setField(user1, "id", userId1);
    User user2 = User.builder()
        .nickname("testUser")
        .build();
    UUID userId2 = UUID.randomUUID();
    ReflectionTestUtils.setField(user1, "id", userId2);

    Review review1 = Review.builder()
        .content("test Content1")
        .book(book)
        .user(user1)
        .rating(5)
        .likeCount(10)
        .commentCount(5)
        .build();
    ReflectionTestUtils.setField(review1, "id", reviewId1);

    Review review2 = Review.builder()
        .content("test Content2")
        .book(book)
        .user(user2)
        .rating(3)
        .likeCount(50)
        .commentCount(10)
        .build();
    ReflectionTestUtils.setField(review2, "id", reviewId2);

    List<Review> reviews = List.of(review1, review2);

    // 대시보드 설정
    List<Dashboard> dashboards = List.of(
        Dashboard.builder()
            .key(reviewId1)
            .keyType(KeyType.REVIEW)
            .period(period)
            .value(80.0)
            .valueType(ValueType.SCORE)
            .rank(2)
            .build(),
        Dashboard.builder()
            .key(reviewId2)
            .keyType(KeyType.REVIEW)
            .period(period)
            .value(90.0)
            .valueType(ValueType.SCORE)
            .rank(1)
            .build()

    );

    when(reviewRepository.findByIsDeletedFalse()).thenReturn(reviews);
    when(dataRangeUtil.getDateRange(period)).thenReturn(dateRange);

    // 좋아요, 댓글 카운트 설정
    when(likeRepository.countByReviewIdAndCreatedAtBetween(
        eq(reviewId1),
        any(LocalDateTime.class),
        any(LocalDateTime.class))).thenReturn(5L);
    when(likeRepository.countByReviewIdAndCreatedAtBetween(
        eq(reviewId2),
        any(LocalDateTime.class),
        any(LocalDateTime.class))).thenReturn(20L);
    when(commentRepository.countByReviewIdAndCreatedAtBetweenAndIsDeletedFalse(
        eq(reviewId1),
        any(LocalDateTime.class),
        any(LocalDateTime.class))).thenReturn(3L);
    when(commentRepository.countByReviewIdAndCreatedAtBetweenAndIsDeletedFalse(
        eq(reviewId2),
        any(LocalDateTime.class),
        any(LocalDateTime.class))).thenReturn(5L);
    when(assignReviewRank.assignRank(
        any(Map.class),
        eq(period),
        eq(KeyType.REVIEW),
        any(List.class)
    )).thenReturn(dashboards);

    // when
    popularReviewDashboardBatchService.savePopularReviewDashboardData(period);

    verify(reviewRepository).findByIsDeletedFalse();
    verify(dashboardRepository).saveAll(any());
    verify(likeRepository, times(2)).countByReviewIdAndCreatedAtBetween(any(), any(), any());
    verify(commentRepository, times(2)).countByReviewIdAndCreatedAtBetweenAndIsDeletedFalse(any(),
        any(), any());
    verify(dashboardRepository).saveAll(any());
  }


}