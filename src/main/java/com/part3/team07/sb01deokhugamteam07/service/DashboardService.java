package com.part3.team07.sb01deokhugamteam07.service;


import com.part3.team07.sb01deokhugamteam07.dto.book.PopularBookDto;
import com.part3.team07.sb01deokhugamteam07.dto.book.response.CursorPageResponsePopularBookDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.PopularReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.response.CursorPageResponsePopularReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.PowerUserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.UserMetricsDTO;
import com.part3.team07.sb01deokhugamteam07.dto.user.response.CursorPageResponsePowerUserDto;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.entity.ValueType;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookNotFoundException;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepository;
import com.part3.team07.sb01deokhugamteam07.repository.querydsl.DashboardRepositoryCustom;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class DashboardService {

  private final DashboardRepository dashboardRepository;
  private final DashboardRepositoryCustom dashboardRepositoryCustom;
  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;
  private final BookRepository bookRepository;


  /**
   * 대시보드 삭제 기능
   * **/
  public void delete(){
    dashboardRepository.deleteAll();
  }


  /**
   * Power User 조회합니다.
   *
   * @param period    조회할 기간 (e.g. DAILY, WEEKLY, MONTHLY, ALL_TIME)
   * @param direction 정렬 뱡향 (e.g. asc(default), desc)
   * @param cursor    기준이 되는 rank
   * @param after     createAt 기반 보조 커서
   * @param limit     가져올 개수
   * @return 커서 기반 페이지 응답 DTO ( content(PowerUser), nextCursor, nextAfter, size, totalElement,
   * hasNext )
   **/
  public CursorPageResponsePowerUserDto getPowerUsers(
      Period period,
      String direction,
      String cursor,
      String after,
      int limit) {
    log.info("getPowerUsers 호출: period={}, direction={}, cursor={}, after={}, limit={}",
        period, direction, cursor, after, limit);

    // 1. 커스텀 레포지토리에서 Power User 대시보드 조회
    List<Dashboard> dashboards = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(
        period,
        direction,
        cursor,
        after,
        limit + 1,
        KeyType.USER); // + 1 다음 페이지 존재 여부 확인

    // 없을시 조기 반환
    if(dashboards.isEmpty()){
      long totalElement = dashboardRepository.countByKeyTypeAndPeriod(KeyType.USER, period);
      return new CursorPageResponsePowerUserDto(
          List.of(),
          null,
          null,
          0,
          totalElement,
          false
      );
    }

    // 2. 다음 페이지 존재 여부 판단 및 실제 리스트 잘라내기
    boolean hasNext = dashboards.size() > limit;
    if (hasNext) {
      dashboards = dashboards.subList(0, limit);
    }

    // 3. 대시보드의 key (User ID) 기준으로 사용자 정보 조회
    List<UUID> userIds = dashboards.stream()
        .map(Dashboard::getKey)
        .toList();
    List<User> users = userRepository.findAllById(userIds);

    // 4. 사용자 ID -> User 객체 매핑 (빠른 접근을 위해 Map 으로 변환)
    Map<UUID, User> userMap = users.stream()
        .collect(Collectors.toMap(User::getId, user -> user));

    // 5. 사용자별 추가 지표 정보 (리뷰점수합, 좋아요수, 댓글수) 조회
    List<UserMetricsDTO> userMetrics = dashboardRepository.getUserMetrics(period.name());

    Map<UUID, UserMetricsDTO> metricsDTOMap = userMetrics.stream()
        .collect(Collectors.toMap(UserMetricsDTO::userId, Function.identity()));

    // 6. PowerUserDto 변환
    List<PowerUserDto> content = new ArrayList<>();
    for (Dashboard d : dashboards) {
      UUID userId = d.getKey();
      User user = userMap.get(userId);

      // 유저 지표 정보
      UserMetricsDTO metrics = metricsDTOMap.get(userId);

      BigDecimal reviewScoreSum =
          metrics != null && metrics.reviewScoreSum() != null ? metrics.reviewScoreSum() : BigDecimal.ZERO;
      int likeCount =
          metrics != null && metrics.likeCount() != null ? metrics.likeCount().intValue() : 0;
      int commentCount =
          metrics != null && metrics.commentCount() != null ? metrics.commentCount().intValue() : 0;

      if (user != null) {
        content.add(
            new PowerUserDto(
                d.getKey(),
                user.getNickname(),
                period,
                d.getCreatedAt(),
                d.getRank(),
                d.getValue(),
                reviewScoreSum,
                likeCount,
                commentCount
            )
        );
      }
    }

    // 7. 다음 페이지 커서 및 after 값 설정
    String nextCursor =
        hasNext ? String.valueOf(dashboards.get(dashboards.size() - 1).getRank()) : null;
    LocalDateTime nextAfter = hasNext ? dashboards.get(dashboards.size() - 1).getCreatedAt() : null;

    // 8. 전체 User 수 (기간 + USER 키타입 조건)
    long totalElement = dashboardRepository.countByKeyTypeAndPeriodAndValueType(KeyType.USER, period, ValueType.SCORE);

    log.info("Power User 조회 완료: 총 {}명 중 {}명 반환, 다음 페이지: {}",
        totalElement, content.size(), hasNext);

    return new CursorPageResponsePowerUserDto(
        content,
        nextCursor,
        nextAfter,
        limit,
        totalElement,
        hasNext
    );
  }

  /**
   * Popular Review 조회합니다.
   *
   * @param period    조회할 기간 (e.g. DAILY, WEEKLY, MONTHLY, ALL_TIME)
   * @param direction 정렬 뱡향 (e.g. asc(default), desc)
   * @param cursor    기준이 되는 rank
   * @param after     createAt 기반 보조 커서
   * @param limit     가져올 개수
   * @return 커서 기반 페이지 응답 DTO ( content(PopularReviewDto), nextCursor, nextAfter, size,
   * totalElement, hasNext )
   **/
  public CursorPageResponsePopularReviewDto getPopularReviews(
      Period period,
      String direction,
      String cursor,
      String after,
      int limit) {
    log.info("getPopularReviews 호출: period={}, direction={}, cursor={}, after={}, limit={}",
        period, direction, cursor, after, limit);

    // 1. 커스텀 레포지토리에서 Popular Review 대시보드 조회
    List<Dashboard> dashboards = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(
        period,
        direction,
        cursor,
        after,
        limit + 1,
        KeyType.REVIEW); // + 1 다음 페이지 존재 여부 확인

    // 없을시 조기 반환
    if(dashboards.isEmpty()){
      long totalElement = dashboardRepository.countByKeyTypeAndPeriod(KeyType.REVIEW, period);
      return new CursorPageResponsePopularReviewDto(
          List.of(),
          null,
          null,
          0,
          totalElement,
          false
      );
    }

    // 2. 다음 페이지 존재 여부 판단 및 실제 리스트 잘라내기
    boolean hasNext = dashboards.size() > limit;
    if (hasNext) {
      dashboards = dashboards.subList(0, limit);
    }

    // 3. 대시보드의 key (Review ID) 기준으로 리뷰 정보 조회
    List<UUID> reviewIds = dashboards.stream()
        .map(Dashboard::getKey)
        .toList();
    List<Review> reviews = reviewRepository.findAllById(reviewIds);

    // 4. 리뷰 ID -> Reviews 객체 매핑 (빠른 접근을 위해 Map 으로 변환)
    Map<UUID, Review> reviewMap = reviews.stream()
        .collect(Collectors.toMap(Review::getId, review -> review));

    // 5. PopularReviewDto 변환
    List<PopularReviewDto> content = new ArrayList<>();
    for (Dashboard d : dashboards) {
      UUID reviewId = d.getKey();
      Review review = reviewMap.get(reviewId);
      if (review != null) {
        content.add(
            new PopularReviewDto(
                d.getId(), // key 와 id 는 다릅니다
                reviewId,
                review.getBook().getId(),
                review.getBook().getTitle(),
                review.getBook().getThumbnailUrl(),
                review.getUser().getId(),
                review.getUser().getNickname(),
                review.getContent(),
                BigDecimal.valueOf(review.getRating()),
                period,
                d.getCreatedAt(),
                d.getRank(),
                d.getValue(),
                review.getLikeCount(),
                review.getCommentCount()
            )
        );
      }
    }

    // 6. 다음 페이지 커서 및 after 값 설정
    String nextCursor =
        hasNext ? String.valueOf(dashboards.get(dashboards.size() - 1).getRank()) : null;
    LocalDateTime nextAfter = hasNext ? dashboards.get(dashboards.size() - 1).getCreatedAt() : null;

    // 7. 전체 REVIEW 수 (기간 + REVIEW 키타입 조건)
    long totalElement = dashboardRepository.countByKeyTypeAndPeriod(KeyType.REVIEW, period);

    log.info("Popular Review 조회 완료: 총 {}명 중 {}명 반환, 다음 페이지: {}",
        totalElement, content.size(), hasNext);

    return new CursorPageResponsePopularReviewDto(
        content,
        nextCursor,
        nextAfter,
        limit,
        totalElement,
        hasNext
    );
  }

  /**
   * Popular Book 조회합니다.
   *
   * @param period    조회할 기간 (e.g. DAILY, WEEKLY, MONTHLY, ALL_TIME)
   * @param direction 정렬 뱡향 (e.g. asc(default), desc)
   * @param cursor    기준이 되는 rank
   * @param after     createAt 기반 보조 커서
   * @param limit     가져올 개수
   * @return 커서 기반 페이지 응답 DTO ( content(PopularBookDto), nextCursor, nextAfter, size, totalElement,
   * hasNext )
   **/
  public CursorPageResponsePopularBookDto getPopularBooks(
      Period period,
      String direction,
      String cursor,
      String after,
      int limit) {
    log.info("getPopularBooks 호출: period={}, direction={}, cursor={}, after={}, limit={}",
        period, direction, cursor, after, limit);

    // 1. 커스텀 레포지토리에서 Popular Book 대시보드 조회
    List<Dashboard> dashboards = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(
        period,
        direction,
        cursor,
        after,
        limit + 1,
        KeyType.BOOK
    );

    // 없을시 조기 반환
    if(dashboards.isEmpty()){
      long totalElement = dashboardRepository.countByKeyTypeAndPeriod(KeyType.BOOK, period);
      return new CursorPageResponsePopularBookDto(
          List.of(),
          null,
          null,
          0,
          totalElement,
          false
      );
    }


    // 2. 다음 페이지 존재 여부 판단 및 실제 리스트 잘라내기
    boolean hasNext = dashboards.size() > limit;
    if (hasNext) {
      dashboards = dashboards.subList(0, limit);
    }

    // 3. 대시보드의 key (Book ID) 기준으로 도서 정보 조회
    List<UUID> bookIds = dashboards.stream()
        .map(Dashboard::getKey)
        .toList();
    List<Book> books = bookRepository.findAllById(bookIds);
    if(books.isEmpty()){
      throw new BookNotFoundException();
    }

    // 4. 도서 ID -> Book 객체 매핑 (빠른 접근을 위해 Map 으로 변환)
    Map<UUID, Book> bookMap = books.stream()
        .collect(Collectors.toMap(Book::getId, book -> book));

    // 5. PopularBookDto 변환
    List<PopularBookDto> content = new ArrayList<>();
    for (Dashboard d : dashboards) {
      UUID bookId = d.getKey();
      Book book = bookMap.get(bookId);
      if (book != null) {
        content.add(
            new PopularBookDto(
                d.getId(),
                bookId,
                book.getTitle(),
                book.getAuthor(),
                book.getThumbnailUrl(),
                period,
                d.getRank(),
                d.getValue(),
                book.getReviewCount(),
                book.getRating(),
                d.getCreatedAt()
            )
        );
      }
    }

    // 6. 다음 페이지 커서 및 after 값 설정
    String nextCursor =
        hasNext ? String.valueOf(dashboards.get(dashboards.size() - 1).getRank())
            : null;
    LocalDateTime nextAfter = hasNext ? dashboards.get(dashboards.size() - 1).getCreatedAt() : null;

    // 7. 전체 BOOK 수 (기간 + BOOK 키타입 조건)
    long totalElement = dashboardRepository.countByKeyTypeAndPeriod(KeyType.BOOK, period);

    log.info("Popular Book 조회 완료: 총 {}명 중 {}명 반환, 다음 페이지: {}",
        totalElement, content.size(), hasNext);
    return new CursorPageResponsePopularBookDto(
        content,
        nextCursor,
        nextAfter,
        limit,
        totalElement,
        hasNext
    );
  }
}
