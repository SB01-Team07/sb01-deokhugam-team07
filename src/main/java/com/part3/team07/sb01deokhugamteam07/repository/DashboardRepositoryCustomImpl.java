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
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class DashboardRepositoryCustomImpl implements DashboardRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  // 생성자: JPAQueryFactory 주입
  public DashboardRepositoryCustomImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

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
    if (cursor != null) { // cursor(rank) 기준 정렬
      try {
        int rankCursor = Integer.parseInt(cursor);
        if (isAsc) {
          builder.and(dashBoard.rank.gt(rankCursor));
        } else {
          builder.and(dashBoard.rank.lt(rankCursor));
        }
      } catch (NumberFormatException e) {
        log.warn("findPowerUsersByPeriod()에 전달된 잘못된 cursor 값: {} : ", cursor);
      }
    } else if (after != null) { // after(createdAt) 보조 정렬 *cursor 이 없을시에만, 안정성 용도
      try {
        LocalDateTime afterTime = LocalDateTime.parse(after);
        if (isAsc) {
          builder.and(dashBoard.createdAt.gt(afterTime));
        } else {
          builder.and(dashBoard.createdAt.lt(afterTime));
        }
      } catch (DateTimeException e) {
        log.warn("findPowerUsersByPeriod()에 전달된 잘못된 after 값: {} : ", after);
      }
    } else { // 첫 페이지
      builder.and(dashBoard.rank.gt(0));
    }

    // 랭킹 기준으로 정렬
    OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(orderDirection, dashBoard.rank);

    // 결과 조회
    return queryFactory
        .selectFrom(dashBoard)
        .where(builder)
        .orderBy(orderSpecifier)
        .limit(limit)
        .fetch();
  }
}
