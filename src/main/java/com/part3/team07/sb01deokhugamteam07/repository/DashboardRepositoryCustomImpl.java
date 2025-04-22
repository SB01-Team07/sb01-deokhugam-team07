package com.part3.team07.sb01deokhugamteam07.repository;


import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.QDashboard;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.ValueType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class DashboardRepositoryCustomImpl implements DashboardRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  /**
   * 파워 유저 대시보드를 조회합니다. 커서 기반의 페이지네이션을 지원하며, 주어진 기간에 대한 유저의 랭킹과 그에 해당하는 데이터를 반환합니다.
   *
   * @param period    조회할 기간 (e.g. DAILY, WEEKLY, MONTHLY, ALL_TIME)
   * @param direction 정렬 뱡향 (e.g. asc(default), desc)
   * @param after     createAt 기반 보조 커서
   * @param limit     가져올 개수
   * @return (KeyType : User, ValueType : SCORE)인 커서 페이지네이션 기반 Dashboard 리스트
   **/
  @Override
  public List<Dashboard> findPowerUsersByPeriod(Period period, String direction, String cursor,
      String after, int limit) {

    QDashboard dashboard = QDashboard.dashboard;

    // 정렬 방향 설정
    boolean isAsc = "asc".equalsIgnoreCase(direction);
    Order orderDirection = isAsc ? Order.ASC : Order.DESC;

    // 기본 조건 : 기간, 키타입, 값타입 설정
    BooleanBuilder builder = new BooleanBuilder()
        .and(dashboard.period.eq(period))
        .and(dashboard.keyType.eq(KeyType.USER))
        .and(dashboard.valueType.eq(ValueType.SCORE));

    // 커서 기반 페이지네이션 처리
    builder = addCursorCondition(builder, dashboard, cursor, after, isAsc);

    // 랭킹 기준으로 정렬
    OrderSpecifier<?> orderByRank = new OrderSpecifier<>(orderDirection, dashboard.rank);
    OrderSpecifier<?> orderByCreatedAt = new OrderSpecifier<>(orderDirection, dashboard.createdAt);

    // 결과 조회
    return queryFactory
        .selectFrom(dashboard)
        .where(builder)
        .orderBy(orderByRank, orderByCreatedAt)
        .limit(limit)
        .fetch();
  }


  /**
   * 인기 리뷰 대시보드를 조회합니다. 커서 기반의 페이지네이션을 지원하며, 주어진 기간에 대한 리뷰의 랭킹과 그에 해당하는 데이터를 반환합니다. 유저와 다르게
   * VALUE_TYPE:SCORE 만 존재합니다.
   *
   * @param period    조회할 기간 (e.g. DAILY, WEEKLY, MONTHLY, ALL_TIME)
   * @param direction 정렬 뱡향 (e.g. asc(default), desc)
   * @param after     createAt 기반 보조 커서
   * @param limit     가져올 개수
   * @return (KeyType : Review, ValueType : SCORE)인 커서 페이지네이션 기반 Dashboard 리스트
   **/
  @Override
  public List<Dashboard> findPopularReviewByPeriod(Period period, String direction, String cursor,
      String after, int limit) {

    QDashboard dashboard = QDashboard.dashboard;

    // 정렬 방향 설정
    boolean isAsc = "asc".equalsIgnoreCase(direction);
    Order orderDirection = isAsc ? Order.ASC : Order.DESC;

    // 기본 조건 : 기간, 키타입, 값타입 설정
    BooleanBuilder builder = new BooleanBuilder()
        .and(dashboard.period.eq(period))
        .and(dashboard.keyType.eq(KeyType.REVIEW))
        .and(dashboard.valueType.eq(ValueType.SCORE));

    // 커서 기반 페이지네이션 처리
    builder = addCursorCondition(builder, dashboard, cursor, after, isAsc);

    // 랭킹 기준으로 정렬
    OrderSpecifier<?> orderByRank = new OrderSpecifier<>(orderDirection, dashboard.rank);
    OrderSpecifier<?> orderByCreatedAt = new OrderSpecifier<>(orderDirection, dashboard.createdAt);

    // 결과 조회
    return queryFactory
        .selectFrom(dashboard)
        .where(builder)
        .orderBy(orderByRank, orderByCreatedAt)
        .limit(limit)
        .fetch();
  }


  /**
   * Cursor = rank, after = 집계 생성 시각 을 기준으로 페이지네이션으로 하는 메서드
   *
   * @param builder   BooleanBuilder 에 조건을 추가하기 위한 전달
   * @param dashboard QueryDSL 용 QDashboard 객체 (조건에 사용할 필드들 포함)
   * @param cursor    기준이 되는 rank
   * @param after     기준이 되는 생성 시간
   * @param isAsc     정렬 방향이 오름차순인지 여부
   *
   * @return 조건이 추가된 BooleanBuilder
   **/
  private BooleanBuilder addCursorCondition(
      BooleanBuilder builder,
      QDashboard dashboard,
      String cursor,
      String after,
      boolean isAsc
  ) {

    try {
      if (cursor != null && after != null) {  // cursor와 after 둘 다 있을 경우
        int rankCursor = Integer.parseInt(cursor);
        LocalDateTime afterTime = LocalDateTime.parse(after);

        if (isAsc) {
          return builder.and(
              dashboard.rank.gt(rankCursor) // rankCursor보다 더 높은 순위
                  .or(dashboard.rank.eq(rankCursor) // rankCursor == rank인 사람도 고려 (동점자 처리)
                      .and(dashboard.createdAt.gt(afterTime)) // 동점자 중에서 커서 기준 이후인 사람만
                  )
          );
        } else {
          return builder.and(
              dashboard.rank.lt(rankCursor) // 내림차순일 때는 rankCursor보다 작은 값
                  .or(dashboard.rank.eq(rankCursor) // 동점자 처리
                      .and(dashboard.createdAt.lt(afterTime)) // 동점자 중에서 커서 기준 이전인 사람만
                  )
          );
        }

      } else if (cursor != null) {  // cursor만 있을 경우
        int rankCursor = Integer.parseInt(cursor);

        if (isAsc) {
          return builder.and(dashboard.rank.gt(rankCursor)); // rankCursor보다 높은 순위만
        } else {
          return builder.and(dashboard.rank.lt(rankCursor)); // rankCursor보다 낮은 순위만
        }

      } else {  // 첫 페이지
        return builder.and(dashboard.rank.gt(0)); // 첫 페이지에서는 rank가 0보다 큰 것들만
      }
    } catch (NumberFormatException | DateTimeException e) {
      log.warn("잘못된 커서 값이 들어왔습니다. cursor={}, after={}, e={}", cursor, after, e.toString());
      return builder.and(dashboard.rank.gt(0)); // 기본값으로 첫 페이지 조건 적용
    }
  }
}
