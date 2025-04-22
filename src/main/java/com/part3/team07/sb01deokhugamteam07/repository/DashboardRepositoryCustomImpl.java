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

    QDashboard dashBoard = QDashboard.dashboard;

    // 정렬 방향 설정
    boolean isAsc = "asc".equalsIgnoreCase(direction);
    Order orderDirection = isAsc ? Order.ASC : Order.DESC;

    // 기본 조건 : 기간, 키타입, 값타입 설정
    BooleanBuilder builder = new BooleanBuilder()
        .and(dashBoard.period.eq(period))
        .and(dashBoard.keyType.eq(KeyType.USER))
        .and(dashBoard.valueType.eq(ValueType.SCORE));

    // 커서 기반 페이지네이션 처리
    try {
      if (cursor != null && after != null) {  // cursor와 after 둘 다 있을 경우
        int rankCursor = Integer.parseInt(cursor);
        LocalDateTime afterTime = LocalDateTime.parse(after);

        if (isAsc) {
          builder.and(
              dashBoard.rank.gt(rankCursor) // rankCursor보다 더 높은 순위
                  .or(dashBoard.rank.eq(rankCursor) // rankCursor == rank인 사람도 고려 (동점자 처리)
                      .and(dashBoard.createdAt.gt(afterTime)) // 동점자 중에서 커서 기준 이후인 사람만
                  )
          );
        } else {
          builder.and(
              dashBoard.rank.lt(rankCursor) // 내림차순일 때는 rankCursor보다 작은 값
                  .or(dashBoard.rank.eq(rankCursor) // 동점자 처리
                      .and(dashBoard.createdAt.lt(afterTime)) // 동점자 중에서 커서 기준 이전인 사람만
                  )
          );
        }

      } else if (cursor != null) {  // cursor만 있을 경우
        int rankCursor = Integer.parseInt(cursor);

        if (isAsc) {
          builder.and(dashBoard.rank.gt(rankCursor)); // rankCursor보다 높은 순위만
        } else {
          builder.and(dashBoard.rank.lt(rankCursor)); // rankCursor보다 낮은 순위만
        }

      } else {  // 첫 페이지
        builder.and(dashBoard.rank.gt(0)); // 첫 페이지에서는 rank가 0보다 큰 것들만
      }
    } catch (NumberFormatException | DateTimeException e) {
      log.warn("잘못된 커서 값이 들어왔습니다. cursor={}, after={}, e={}", cursor, after, e.toString());
    }


    // 랭킹 기준으로 정렬
    OrderSpecifier<?> orderByRank = new OrderSpecifier<>(orderDirection, dashBoard.rank);
    OrderSpecifier<?> orderByCreatedAt = new OrderSpecifier<>(orderDirection, dashBoard.createdAt);

    // 결과 조회
    return queryFactory
        .selectFrom(dashBoard)
        .where(builder)
        .orderBy(orderByRank, orderByCreatedAt)
        .limit(limit)
        .fetch();
  }
}
