
package com.part3.team07.sb01deokhugamteam07.batch.poweruser;

import com.part3.team07.sb01deokhugamteam07.batch.AssignRankUtil;
import com.part3.team07.sb01deokhugamteam07.batch.DateRangeUtil;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PowerUserDashboardBatchService {

  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;
  private final AssignRankUtil assignRank;
  private final DateRangeUtil dateRangeUtil;
  private final LikeRepository likeRepository;
  private final CommentRepository commentRepository;
  private final DashboardRepository dashboardRepository;

  /**
   * 파워 유저 관련 데이터를 dashboard 테이블에 저장합니다.
   *
   * @param period 기간 정보 (e.g. DAILY, WEEKLY, MONTHLY, ALL_TIME)
   **/
  public void savePowerUserDashboardData(Period period) {
    try {
      log.info("savePowerUserDashboardData 호출: period={}", period);

      // 1. 전체 유저 조회 (is_deleted = false)
      List<User> users = userRepository.findByIsDeletedFalse();
      if (users.isEmpty()) {
        log.info("처리할 유저가 없습니다. period={}", period);
        return;
      }

      // 날짜 범위 계산
      LocalDate[] dateRange = dateRangeUtil.getDateRange(period);
      LocalDateTime startDateTime = dateRange[0].atStartOfDay();
      LocalDateTime endDateTime = dateRange[1].plusDays(1).atStartOfDay()
          .minusNanos(1); // e.g. 일요일 25-04-20 00:00:00 + 하루 더하기 - 나노초 빼기 -> 종료일의 마지막 순간

      // 결과 저장용 대시보드 리스트, 유저 점수 맵
      List<Dashboard> dashboards = new ArrayList<>();
      Map<UUID, Double> userScoreMap = new LinkedHashMap<>();

      // 2. 각 유저의 활동 정보 가져오기
      for (User user : users) {
        UUID userId = user.getId();

        // 2-1. 해당 기간(Period) 동안 작성한 리뷰 목록 가져오기
        List<Review> userReviews = reviewRepository.findByUserIdAndCreatedAtBetweenAndIsDeletedFalse(
            userId, startDateTime, endDateTime);

        userReviews.forEach(review -> {
          log.info("리뷰 점수의 재료 왜 안될까요? >>>> 리뷰 id = {}, like = {}, comment = {}",
              review.getId(), review.getLikeCount(), review.getCommentCount());
        });

        // 2-2. 리뷰 점수 (리뷰당 like*0.3 + comment*0.7) 합계 구하기
        double reviewScoreSum = userReviews.stream()
            .mapToDouble(review -> (review.getLikeCount() * 0.3) + (review.getCommentCount() * 0.7))
            .sum();

        log.info("리뷰 점수... 왜 안될까요? >>>> reviewScoreSum = {}", reviewScoreSum);

        BigDecimal reviewScoreSumBigDecimal = BigDecimal.valueOf(reviewScoreSum);

        // 2-3. 해당 기간 동안 좋아요한 수
        long likeCount = likeRepository.countByUserIdAndCreatedAtBetween(userId, startDateTime,
            endDateTime);

        log.info("likeCount... 왜 안될까요? >>>> likeCount = {}", likeCount);


        BigDecimal likeCountBigDecimal = BigDecimal.valueOf(likeCount);
        log.info("likeCountBigDecimal... 왜 안될까요? >>>> likeCountBigDecimal = {}", likeCountBigDecimal);


        // 2-4. 해당 기간 동안 댓글 단 수
        long commentCount = commentRepository.countByUserIdAndCreatedAtBetweenAndIsDeletedFalse(
            userId, startDateTime,
            endDateTime);
        log.info("commentCount... 왜 안될까요? >>>> commentCount = {}", commentCount);
        BigDecimal commentCountBigDecimal = BigDecimal.valueOf(commentCount);
        log.info("commentCountBigDecimal... 왜 안될까요? >>>> commentCount = {}", commentCountBigDecimal);

        // 3. 활동 점수 계산
        double score = calculateScore(reviewScoreSum, likeCount, commentCount);

        userScoreMap.put(userId, score);

        // 4. REVIEW_SCORE_SUM, LIKE_COUNT, COMMENT_COUNT 지표의 대시보드 데이터 구성
        dashboards.add(
            new Dashboard(userId, KeyType.USER, period, reviewScoreSumBigDecimal,
                ValueType.REVIEW_SCORE_SUM,
                null));
        dashboards.add(
            new Dashboard(userId, KeyType.USER, period, likeCountBigDecimal, ValueType.LIKE_COUNT,
                null));
        dashboards.add(
            new Dashboard(userId, KeyType.USER, period, commentCountBigDecimal,
                ValueType.COMMENT_COUNT, null));
      }

      // 5. SCORE 기준으로 전체 유저 순위 매기기 -> 정렬 후 rank 지정
      dashboards = assignRank.assignRank(userScoreMap, period, KeyType.USER, dashboards);

      // 6. 데이터베이스에 저장
      dashboardRepository.saveAll(dashboards);
      log.info("대시보드에 파워 유저 데이터가 저장됐습니다.: period={}, 저장된 대시보드 개수={}", period, dashboards.size());
    } catch (DataAccessException e) {
      log.error("데이터베이스 접근 중 오류 발생: period={}, error={}", period, e.getMessage(), e);
    } catch (Exception e) {
      log.error(" 파워 유저 대시보드 작업 중 오류 발생: period={}, error={}", period, e.getMessage(), e);
    }
  }

  /**
   * 주어진 리뷰 점수, 좋아요 수, 댓글 수를 바탕으로 유저의 활동 점수를 계산하는 메서드.
   *
   * @param reviewScore  유저의 리뷰 점수
   * @param likeCount    유저가 받은 좋아요 수
   * @param commentCount 유저가 작성한 댓글 수
   * @return 계산된 활동 점수
   **/
  private double calculateScore(double reviewScore, long likeCount, long commentCount) {
    return (reviewScore * 0.5) + (likeCount * 0.2) + (commentCount * 0.3);
  }
}
