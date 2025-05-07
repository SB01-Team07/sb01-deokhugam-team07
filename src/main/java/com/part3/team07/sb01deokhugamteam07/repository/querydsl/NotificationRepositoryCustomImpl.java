package com.part3.team07.sb01deokhugamteam07.repository.querydsl;

import com.part3.team07.sb01deokhugamteam07.entity.Notification;
import com.part3.team07.sb01deokhugamteam07.entity.QNotification;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  /**
   * 알림을 조회합니다. 커서 기반의 페이지네이션을 지원하며, 유저 이름을 기준으로 조회합니다.
   *
   * @param userId    알림을 조회할 사용자 ID
   * @param direction 정렬 방향 (asc 또는 desc, 기본값은 desc)
   * @param cursor    특정 시간 이후의 알림만 조회 (동일)
   * @param after     특정 시간 이후의 알림만 조회 (동일)
   * @param limit     한 페이지에 조회할 알림 개수 (기본값 20)
   * @return 커서 페이지네이션 기반 Notification 리스트
   **/
  @Override
  public List<Notification> findByUserIdWithCursor(UUID userId, String direction, String cursor,
      String after, int limit) {

    QNotification notification = QNotification.notification;

    // 정렬 방향 설정
    boolean isDesc = "desc".equalsIgnoreCase(direction);
    Order orderDirection = isDesc ? Order.DESC : Order.ASC;

    // 기본 조건 : userId
    BooleanBuilder builder = new BooleanBuilder()
        .and(notification.userId.eq(userId));

    if (cursor != null) {
      try {
        LocalDateTime cursorDt = LocalDateTime.parse(cursor);
        if (isDesc) {
          builder.and(notification.createdAt.lt(cursorDt));
        } else {
          builder.and(notification.createdAt.gt(cursorDt));
        }
      } catch (DateTimeException e) {
        log.warn("잘못된 커서 값이 들어왔습니다. cursor={}, after={}, e={}", cursor, after, e.toString());
      }
    } // if(cursor == null) 일시 userId 필터링 & 정렬 방향대로 정렬 & limit 개수 대로만 전달 

    OrderSpecifier<?> orderByDesc = new OrderSpecifier<>(orderDirection, notification.createdAt);

    return queryFactory
        .selectFrom(notification)
        .where(builder)
        .orderBy(orderByDesc)
        .limit(limit)
        .fetch();
  }
}
