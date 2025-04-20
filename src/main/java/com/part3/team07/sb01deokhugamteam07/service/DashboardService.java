package com.part3.team07.sb01deokhugamteam07.service;


import com.part3.team07.sb01deokhugamteam07.dto.user.PowerUserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.response.CursorPageResponsePowerUserDto;
import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.entity.ValueType;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepository;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepositoryCustom;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

  /**
   * Power User 조회합니다.
   *
   * @param period    조회할 기간 (e.g. DAILY, WEEKLY, MONTHLY, ALL_TIME)
   * @param direction 정렬 뱡향 (e.g. asc(default), desc)
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
    List<Dashboard> dashboards = dashboardRepositoryCustom.findPowerUsersByPeriod(
        period,
        direction,
        cursor,
        after,
        limit + 1); // + 1 다음 페이지 존재 여부 확인

    // 2. 다음 페이지 존재 여부 판단 및 실제 리스트 잘라내기
    boolean hasNext = dashboards.size() > limit;
    if (hasNext) {
      dashboards = dashboards.subList(0, limit);
    }

    // 3. 대시보드의 key (User ID) 기준으로 사용자 정보 조회
    List<UUID> userIds = dashboards.stream()
        .map(Dashboard::getKey)
        .toList();
    // TODO User 쪽 "없을 때"관련 커스텀 예외가 있다면 추후에 적용
    List<User> users = userRepository.findAllById(userIds);

    // 4. 사용자 ID -> User 객체 매핑 (빠른 접근을 위해 Map 으로 변환)
    Map<UUID, User> userMap = users.stream()
        .collect(Collectors.toMap(User::getId, user -> user));

    // 5. 사용자별 추가 지표 정보 (리뷰점수합, 좋아요수, 댓글수) 조회
    Map<UUID, Map<String, Double>> userMetrics = dashboardRepository.getUserMetrics(period);

    // 6. PowerUserDto 변환
    List<PowerUserDto> content = new ArrayList<>();
    for (Dashboard d : dashboards) {
      User user = userMap.get(d.getKey());
      Map<String, Double> metrics = userMetrics.getOrDefault(d.getKey(), Map.of());
      double reviewScoreSum = metrics.get(ValueType.REVIEW_SCORE_SUM.name());
      int likeCount = metrics.get(ValueType.LIKE_COUNT.name()).intValue();
      int commentCount = metrics.get(ValueType.COMMENT_COUNT.name()).intValue();

      if (user != null) {
        content.add(
            new PowerUserDto(
                d.getKey(),
                user.getNickname(),
                period,
                user.getCreatedAt(),
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
    long totalElement = dashboardRepository.countByKeyTypeAndPeriod(KeyType.USER, period);

    log.info("Power User 조회 완료: 총 {}명 중 {}명 반환, 다음 페이지: {}",
        totalElement, content.size(), hasNext);

    return new CursorPageResponsePowerUserDto(
        content,
        nextCursor,
        nextAfter,
        content.size(),
        totalElement,
        hasNext
    );
  }
}
