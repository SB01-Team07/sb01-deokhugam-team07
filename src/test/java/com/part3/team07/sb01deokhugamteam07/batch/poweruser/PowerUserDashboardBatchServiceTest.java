package com.part3.team07.sb01deokhugamteam07.batch.poweruser;


import static org.assertj.core.api.Assertions.assertThat;
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
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
class PowerUserDashboardBatchServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private ReviewRepository reviewRepository;
  @Mock
  private DateRangeUtil dateRangeUtil;
  @Mock
  private LikeRepository likeRepository;
  @Mock
  private CommentRepository commentRepository;
  @Mock
  private DashboardRepository dashboardRepository;

  @InjectMocks
  private PowerUserDashboardBatchService powerUserDashboardBatchService;

//  @Test
//  @DisplayName("rank 의 기준이 되는 score 계산 로직")
//  void UserScore_Calculation() {
//    double reviewScore = 80;
//    int likeCount = 10;
//    int commentCount = 5;
//
//    double score = powerUserDashboardBatchService.calculateScore(reviewScore, likeCount,
//        commentCount);
//    assertThat(score).isEqualTo(43.5);
//  }

  @Test
  @DisplayName("score 정렬 후 rank 지정하기")
  void AssignRank_BasedOnScore() {
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    UUID userId3 = UUID.randomUUID();
    UUID userId4 = UUID.randomUUID();

    Map<UUID, Double> userScoreMap = new HashMap<>();

    userScoreMap.put(userId1, 70.0);
    userScoreMap.put(userId2, 90.0);
    userScoreMap.put(userId3, 80.0);
    userScoreMap.put(userId4, 80.0);

    Period period = Period.WEEKLY;
    KeyType keyType = KeyType.USER;

    List<Dashboard> dashboards = new ArrayList<>();

    List<Dashboard> assignUserRank = powerUserDashboardBatchService.assignUserRank(userScoreMap,
        period, keyType, dashboards);

    assertThat(assignUserRank).hasSize(4);

    ReflectionTestUtils.setField(assignUserRank.get(0), "id", userId2);
    ReflectionTestUtils.setField(assignUserRank.get(1), "id", userId3);
    ReflectionTestUtils.setField(assignUserRank.get(2), "id", userId4);
    ReflectionTestUtils.setField(assignUserRank.get(3), "id", userId1);

    // 확인 용 Map
    Map<UUID, Integer> expectedRanks = Map.of(
        userId2, 1,
        userId3, 2,
        userId4, 2,
        userId1, 4
    );

    assertThat(
        assignUserRank.stream().filter(d -> d.getId().equals(userId3)).findFirst().get().getRank()
    ).isEqualTo(
        assignUserRank.stream().filter(d -> d.getId().equals(userId4)).findFirst().get().getRank()
    );

    for (Dashboard d : assignUserRank) {
      UUID id = d.getId();
      assertThat(d.getRank()).isEqualTo(expectedRanks.get(id));
      assertThat(d.getValue()).isEqualTo(userScoreMap.get(id));
      assertThat(d.getValueType()).isEqualTo(ValueType.SCORE);
    }
  }

  @Test
  @DisplayName("파워 유저 대시보드 데이터 저장 성공")
  void savePowerUserDashboardData_success() {
    Period period = Period.WEEKLY;

    // 사용자 설정
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();

    User user1 = User.builder()
        .nickname("testUser1")
        .password("password12")
        .email("test1@domain.com")
        .build();
    ReflectionTestUtils.setField(user1, "id", userId1);

    User user2 = User.builder()
        .nickname("testUser2")
        .password("password34")
        .email("test2@domain.com")
        .build();
    ReflectionTestUtils.setField(user2, "id", userId2);

    List<User> users = List.of(user1, user2);

    // Period 설정
    LocalDate[] dateRange = new LocalDate[]{
        LocalDate.of(2025, 4, 15),
        LocalDate.of(2025, 4, 21),
    };

    when(dateRangeUtil.getDateRange(period)).thenReturn(dateRange);
    when(userRepository.findByIsDeletedFalse()).thenReturn(users);

    // 각 사용자 별 리뷰 설정
    List<Review> user1Reviews = new ArrayList<>();
    Book book1 = Book.builder().build();
    Review review1 = Review.builder()
        .user(user1)
        .book(book1)
        .content("test review 1")
        .rating(4)
        .likeCount(5)
        .commentCount(10)
        .build();
    user1Reviews.add(review1);

    List<Review> user2Reviews = new ArrayList<>();
    Book book2 = Book.builder().build();
    Review review2 = Review.builder()
        .user(user2)
        .book(book2)
        .content("test review 2")
        .rating(3)
        .likeCount(7)
        .commentCount(15)
        .build();
    user1Reviews.add(review2);

    when(reviewRepository.findByUserIdAndCreatedAtBetweenAndIsDeletedFalse(
        eq(userId1), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(user1Reviews);

    when(reviewRepository.findByUserIdAndCreatedAtBetweenAndIsDeletedFalse(
        eq(userId2), any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(user2Reviews);

    // 좋아요, 댓글 카운트 설정
    when(likeRepository.countByUserIdAndCreatedAtBetween(eq(userId1), any(LocalDateTime.class),
        any(LocalDateTime.class))).thenReturn(15L);

    when(likeRepository.countByUserIdAndCreatedAtBetween(eq(userId2), any(LocalDateTime.class),
        any(LocalDateTime.class))).thenReturn(5L);

    when(commentRepository.countByUserIdAndCreatedAtBetweenAndIsDeletedFalse(eq(userId1),
        any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(20L);

    when(commentRepository.countByUserIdAndCreatedAtBetweenAndIsDeletedFalse(eq(userId2),
        any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(3L);

    // when
    powerUserDashboardBatchService.savePowerUserDashboardData(period);

    verify(userRepository).findByIsDeletedFalse();
    verify(reviewRepository, times(2)).findByUserIdAndCreatedAtBetweenAndIsDeletedFalse(any(),
        any(), any());
    verify(likeRepository, times(2)).countByUserIdAndCreatedAtBetween(any(), any(), any());
    verify(commentRepository, times(2)).countByUserIdAndCreatedAtBetweenAndIsDeletedFalse(any(),
        any(), any());
    verify(dashboardRepository).saveAll(any());
  }
}