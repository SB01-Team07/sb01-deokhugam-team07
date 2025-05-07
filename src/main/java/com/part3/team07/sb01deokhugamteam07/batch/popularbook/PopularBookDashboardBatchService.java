package com.part3.team07.sb01deokhugamteam07.batch.popularbook;

import com.part3.team07.sb01deokhugamteam07.batch.AssignRankUtil;
import com.part3.team07.sb01deokhugamteam07.batch.DateRangeUtil;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PopularBookDashboardBatchService {

  private final BookRepository bookRepository;
  private final DateRangeUtil dateRangeUtil;
  private final ReviewRepository reviewRepository;
  private final AssignRankUtil assignRankUtil;
  private final DashboardRepository dashboardRepository;


  /**
   * 인기 도서 관련 데이터를 dashboard 테이블에 저장합니다.
   *
   * @param period 기간 정보 (e.g. DAILY, WEEKLY, MONTHLY, ALL_TIME)
   **/
  public void savePopularBookDashboardData(Period period) {
    try {
      log.info("savePopularBookDashboardData 호출: period={}", period);

      // 날짜 범위 계산
      LocalDateTime[] dateTimeRange = dateRangeUtil.getDateRange(period);
      LocalDateTime startDateTime = dateTimeRange[0];
      LocalDateTime endDateTime = dateTimeRange[1];

      List<Book> booksWithReviews = bookRepository.findByBookWithReviewsInPeriod(startDateTime,
          endDateTime);
      if(booksWithReviews.isEmpty()){
        log.info("해당 기간에 리뷰가 있는 도서가 없습니다. period={}", period);
        return;
      }

      // 결과 저장용 대시보드 리스트
      List<Dashboard> dashboards = new ArrayList<>();
      Map<UUID, Double> bookScoreMap = new LinkedHashMap<>();

      // 2. 각 도서의 정보 가져오기
      for (Book book : booksWithReviews) {
        UUID bookId = book.getId();

        List<Review> reviews = reviewRepository.findByBookIdAndCreatedAtBetweenAndIsDeletedFalse(
            bookId,
            startDateTime,
            endDateTime
        );

        // 2-1. 해당 기간 동안의 리뷰 수
        int reviewCount = reviews.size();

        // 2-2. 해당 기간 동안받은 평점의 평균
        double ratingAverage = reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);

        // 3. 점수 계산
        double score = calculateScore(reviewCount, ratingAverage);
        bookScoreMap.put(bookId, score);
      }

      // 4. Score 기준으로 전체 도서 순위 매기기 -> 정렬 후 rank 지정
      dashboards = assignRankUtil.assignRank(bookScoreMap, period, KeyType.BOOK, dashboards);

      // 5. 데이터베이스에 저장
      dashboardRepository.saveAll(dashboards);
      log.info("대시보드에 인기 도서 데이터가 저장됐습니다.: period={}, 저장된 대시보드 개수={}", period, dashboards.size());

    } catch (DataAccessException e) {
      log.error("데이터베이스 접근 중 오류 발생: period={}, error={}", period, e.getMessage(), e);
    } catch (Exception e) {
      log.error("인기 도서 대시보드 작업 중 오류 발생: period={}, error={}", period, e.getMessage(), e);
    }
  }

  /**
   * 리뷰 수, 평점 평균을 바탕으로 도서의 점수를 계산하는 메서드. 점수 = (해당 기간의 리뷰수 * 0.4) + (해당 기간의 평점 평균 * 0.6)
   *
   * @param reviewCount   도서에 작성된 리뷰 수
   * @param ratingAverage 해당 기간의 평점 평균
   * @return 계산된 활동 점수
   **/
  private double calculateScore(int reviewCount, double ratingAverage) {
    return (reviewCount * 0.4) + (ratingAverage * 0.6);
  }

}
