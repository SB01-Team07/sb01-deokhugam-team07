package com.part3.team07.sb01deokhugamteam07.repository;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.part3.team07.sb01deokhugamteam07.entity.Notification;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class NotificationRepositoryCustomImplTest {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private EntityManager entityManager;

  private JPAQueryFactory queryFactory;
  private NotificationRepositoryCustomImpl notificationRepositoryCustom;
  private UUID userId;

  @BeforeEach
  void setup() {

    // JPAQueryFactory 초기화
    queryFactory = new JPAQueryFactory(entityManager);

    // 테스트할 레포지토리 구현체 초기화
    notificationRepositoryCustom = new NotificationRepositoryCustomImpl(queryFactory);

    userId = UUID.randomUUID();

    for (int i = 0; i < 40; i++) {
      Notification notification = Notification.builder()
          .userId(userId)
          .reviewId(UUID.randomUUID())
          .content("[우디]님이 나의 리뷰를 좋아합니다.")
          .confirmed(false)
          .build();

      testEntityManager.persist(notification);
    }
  }

  @Test
  @DisplayName("페이지네이션 동작 여부 확인")
  public void test_Pagination_Limit() {

    String direction = "DESC";
    int limit = 20;

    // 첫 번째 페이지
    List<Notification> firstPage = notificationRepositoryCustom.findByUserIdWithCursor(userId,
        direction, null, null, limit);

    assertEquals(20, firstPage.size());

    // 두 번째 페이지
    String cursor = firstPage.get(firstPage.size() - 1).getCreatedAt().toString();
    String after = cursor;
    List<Notification> secondPage = notificationRepositoryCustom.findByUserIdWithCursor(userId,
        direction, cursor, after, limit);

    // cursor 이후의 데이터가 반환되는지 확인
    assertTrue(firstPage.get(firstPage.size() - 1).getCreatedAt()
        .compareTo(secondPage.get(0).getCreatedAt()) >= 0);
  }

  @Test
  @DisplayName("잘못된 커서 값이 들어온 경우 기본값 처리 확인")
  public void test_Invaild_Cursor_Value() {
    List<Notification> result = notificationRepositoryCustom.findByUserIdWithCursor(userId,
        "desc", "invalid_cursor", null, 20);
    assertEquals(result.size(), 20);
    assertTrue(result.get(0).getCreatedAt()
        .compareTo(result.get(1).getCreatedAt()) >= 0);
  }

  @Test
  @DisplayName("오름차순 내림차순 처리 확인")
  public void test_Desc_Asc(){
    // 내림차순 테스트
    List<Notification> descResult = notificationRepositoryCustom.findByUserIdWithCursor(userId,
        "desc", null, null, 20);

    for (int i = 0; i < descResult.size() - 1; i++) {
      assertTrue(descResult.get(i).getCreatedAt().compareTo(descResult.get(1 + i).getCreatedAt()) >= 0);
    }

    // 오름차순 테스트
    List<Notification> ascResult = notificationRepositoryCustom.findByUserIdWithCursor(
        userId, "asc", null, null, 20);

    for (int i = 0; i < ascResult.size() - 1; i++) {
      assertTrue(ascResult.get(0).getCreatedAt().compareTo(ascResult.get(1).getCreatedAt()) <= 0);
    }
  }
}