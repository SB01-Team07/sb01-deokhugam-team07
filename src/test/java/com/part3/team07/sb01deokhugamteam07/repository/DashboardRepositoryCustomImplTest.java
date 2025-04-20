package com.part3.team07.sb01deokhugamteam07.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.ValueType;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@ActiveProfiles("test")
class DashboardRepositoryCustomImplTest {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private EntityManager entityManager;

  private JPAQueryFactory queryFactory;
  private DashboardRepositoryCustomImpl dashboardRepositoryCustom;

  @BeforeEach
  public void setup() {

    // JPAQueryFactory 초기화
    queryFactory = new JPAQueryFactory(entityManager);

    // 테스트할 레포지토리 구현체 초기화
    dashboardRepositoryCustom = new DashboardRepositoryCustomImpl(queryFactory);

    // 테스트용 데이터 삽입
    for (int i = 1; i <= 30; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.USER)
          .period(Period.DAILY)
          .value(90 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();

      testEntityManager.persist(dashboard);
    }
    for (int i = 1; i <= 30; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.USER)
          .period(Period.WEEKLY)
          .value(90 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();

      testEntityManager.persist(dashboard);
    }
    for (int i = 1; i <= 30; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.USER)
          .period(Period.MONTHLY)
          .value(90 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();

      testEntityManager.persist(dashboard);
    }
    for (int i = 1; i <= 30; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.USER)
          .period(Period.ALL_TIME)
          .value(90 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();

      testEntityManager.persist(dashboard);
    }

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();
  }

  /** limit 이 11 인 이유는 서비스 단에서 hasNext 를 위해 limit + 1 값을 전달하기 때문입니다. **/

  @Test
  @DisplayName("Daily 기간에 대해 데이터 조회")
  public void testfindPowerUsersByDailyWithCursor() {

    // 첫 번째 페이지
    List<Dashboard> firstPage = dashboardRepositoryCustom.findPowerUsersByPeriod(Period.DAILY,
        "asc", null, null, 11);

    assertEquals(11, firstPage.size());
    for (int i = 0; i < firstPage.size() - 1; i++) {
      assertTrue(firstPage.get(i).getRank() < firstPage.get(i + 1).getRank(),
          "랭킹 오름차순 정렬");
    }

    // 두 번째 페이지
    String cursor = String.valueOf(firstPage.get(firstPage.size() - 1).getRank());
    List<Dashboard> secondPage = dashboardRepositoryCustom.findPowerUsersByPeriod(Period.DAILY,
        "asc", cursor, null, 11);

    // cursor 이후의 데이터가 반환되는지 확인
    assertTrue(secondPage.get(0).getRank() > firstPage.get(firstPage.size() - 1).getRank(),
        "두 번째 페이지의 첫 번째 항목은 첫 페이지의 마지막 항목보다 rank가 커야 함");
  }

  @Test
  @DisplayName("Weekly 기간에 대해 데이터 조회")
  public void testfindPowerUsersByWeekly() {
    List<Dashboard> firstPage = dashboardRepositoryCustom.findPowerUsersByPeriod(Period.WEEKLY,
        "asc", null, null, 11);

    assertEquals(11, firstPage.size());
    for (int i = 0; i < firstPage.size() - 1; i++) {
      assertTrue(firstPage.get(i).getRank() < firstPage.get(i + 1).getRank(),
          "랭킹 오름차순 정렬");
    }

    // 두 번째 페이지
    String cursor = String.valueOf(firstPage.get(firstPage.size() - 1).getRank());
    List<Dashboard> secondPage = dashboardRepositoryCustom.findPowerUsersByPeriod(Period.WEEKLY,
        "asc", cursor, null, 11);

    // cursor 이후의 데이터가 반환되는지 확인
    assertTrue(secondPage.get(0).getRank() > firstPage.get(firstPage.size() - 1).getRank(),
        "두 번째 페이지의 첫 번째 항목은 첫 페이지의 마지막 항목보다 rank가 커야 함");

  }

  @Test
  @DisplayName("Monthly 기간에 대해 데이터 조회")
  public void testfindPowerUsersByMonthly() {
    // Monthly 기간에 대해 데이터 조회
    List<Dashboard> firstPage = dashboardRepositoryCustom.findPowerUsersByPeriod(Period.MONTHLY,
        "asc", null, null, 11);

    assertEquals(11, firstPage.size());
    for (int i = 0; i < firstPage.size() - 1; i++) {
      assertTrue(firstPage.get(i).getRank() < firstPage.get(i + 1).getRank(),
          "랭킹 오름차순 정렬");
    }

    // 두 번째 페이지
    String cursor = String.valueOf(firstPage.get(firstPage.size() - 1).getRank());
    List<Dashboard> secondPage = dashboardRepositoryCustom.findPowerUsersByPeriod(Period.MONTHLY,
        "asc", cursor, null, 11);

    // cursor 이후의 데이터가 반환되는지 확인
    assertTrue(secondPage.get(0).getRank() > firstPage.get(firstPage.size() - 1).getRank(),
        "두 번째 페이지의 첫 번째 항목은 첫 페이지의 마지막 항목보다 rank가 커야 함");
  }

  @Test
  @DisplayName("AllTime 기간에 대해 데이터 조회")
  public void testfindPowerUsersByAllTime() {
    // Monthly 기간에 대해 데이터 조회
    List<Dashboard> firstPage = dashboardRepositoryCustom.findPowerUsersByPeriod(Period.ALL_TIME,
        "asc", null, null, 11);

    assertEquals(11, firstPage.size());
    for (int i = 0; i < firstPage.size() - 1; i++) {
      assertTrue(firstPage.get(i).getRank() < firstPage.get(i + 1).getRank(),
          "랭킹 오름차순 정렬");
    }

    // 두 번째 페이지
    String cursor = String.valueOf(firstPage.get(firstPage.size() - 1).getRank());
    List<Dashboard> secondPage = dashboardRepositoryCustom.findPowerUsersByPeriod(Period.ALL_TIME,
        "asc", cursor, null, 11);

    // cursor 이후의 데이터가 반환되는지 확인
    assertTrue(secondPage.get(0).getRank() > firstPage.get(firstPage.size() - 1).getRank(),
        "두 번째 페이지의 첫 번째 항목은 첫 페이지의 마지막 항목보다 rank가 커야 함");
  }
}