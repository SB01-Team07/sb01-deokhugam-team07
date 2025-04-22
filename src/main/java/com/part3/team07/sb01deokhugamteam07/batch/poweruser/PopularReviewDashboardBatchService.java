package com.part3.team07.sb01deokhugamteam07.batch.poweruser;

import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.repository.CommentRepository;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepository;
import com.part3.team07.sb01deokhugamteam07.repository.LikeRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PopularReviewDashboardBatchService {
  private final DateRangeUtil dateRangeUtil;
  private final AssignRankUtil assignReviewRank;
  private final ReviewRepository reviewRepository;
  private final CommentRepository commentRepository;
  private final LikeRepository likeRepository;
  private final DashboardRepository dashboardRepository;

  /**
   * 인기 리뷰 관련 데이터를 dashboard 테이블에 저장합니다.
   *
   * @param period 기간 정보 (e.g. DAILY, WEEKLY, MONTHLY, ALL_TIME)
   **/
  public void savePopularReviewDashboardData(Period period) {
    // 1. 전체 리뷰 조회 (is_deleted = false)
    List<Review> reviews = reviewRepository.findByIsDeletedFalse();

    // 날짜 범위 계산
    LocalDate[] dateRange = dateRangeUtil.getDateRange(period);
    LocalDateTime startDateTime = dateRange[0].atStartOfDay();
    LocalDateTime endDateTime = dateRange[1].plusDays(1).atStartOfDay().minusNanos(1);

    // 결과 저장용 대시보드 리스트
    List<Dashboard> dashboards = new ArrayList<>();
    Map<UUID, Double> reviewScoreMap = new HashMap<>();

    // 2. 각 리뷰의 정보 가져오기
    for (Review review : reviews) {
      UUID reviewId = review.getId();

      // 2-1. 해당 기간 동안 받은 댓글 수
      int commentCount = (int) commentRepository.countByReviewIdAndCreatedAtBetweenAndIsDeletedFalse(
          reviewId,
          startDateTime,
          endDateTime
      );

      // 2-2. 해당 기간 동안 좋아요를 받은 수
      int likeCount = (int) likeRepository.countByReviewIdAndCreatedAtBetween(
          reviewId,
          startDateTime,
          endDateTime
      );

      // 3. 점수 계산
      double score = calculateScore(commentCount, likeCount);
      reviewScoreMap.put(reviewId, score);
    }

    // 5. SCORE 기준으로 전체 리뷰 순위 매기기 -> 메모리에서 정렬 후 rank 지정
    dashboards = assignReviewRank.assignRank(reviewScoreMap, period, KeyType.REVIEW, dashboards);

    // 6. 데이터베이스에 저장
    dashboardRepository.saveAll(dashboards);
  }

  /**
   * 좋아요 수, 댓글 수를 바탕으로 리뷰의 점수를 계산하는 메서드.
   * 점수 = (해당 기간의 좋아요 수 * 0.3) + (해당 기간의 댓글 수 * 0.7)
   *
   * @param likeCount    리뷰가 받은 좋아요 수
   * @param commentCount 리뷰에 작성된 댓글 수
   * @return 계산된 활동 점수
   **/
  private double calculateScore(int likeCount, int commentCount) {
    return (likeCount * 0.3) + (commentCount * 0.7);
  }


}
