package com.part3.team07.sb01deokhugamteam07.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.part3.team07.sb01deokhugamteam07.config.QuerydslConfig;
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
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@EnableJpaAuditing
@Import(QuerydslConfig.class)
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
    for (int i = 1; i <= 20; i++) {
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
    for (int i = 1; i <= 20; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.REVIEW)
          .period(Period.DAILY)
          .value(90 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();

      testEntityManager.persist(dashboard);
    }
    for (int i = 1; i <= 20; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.BOOK)
          .period(Period.DAILY)
          .value(90 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();

      testEntityManager.persist(dashboard);
    }
    for (int i = 1; i <= 20; i++) {
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
    for (int i = 1; i <= 20; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.REVIEW)
          .period(Period.WEEKLY)
          .value(90 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();

      testEntityManager.persist(dashboard);
    }
    for (int i = 1; i <= 20; i++) {
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
    for (int i = 1; i <= 10; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.REVIEW)
          .period(Period.MONTHLY)
          .value(90 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();

      testEntityManager.persist(dashboard);
    }
    for (int i = 1; i <= 10; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.BOOK)
          .period(Period.WEEKLY)
          .value(90 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();

      testEntityManager.persist(dashboard);
    }
    for (int i = 1; i <= 10; i++) {
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
    for (int i = 1; i <= 10; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.REVIEW)
          .period(Period.ALL_TIME)
          .value(90 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();

      testEntityManager.persist(dashboard);
    }
    for (int i = 1; i <= 10; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.BOOK)
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

  @Test
  @DisplayName("KeyType 의 조건에 따라 대시보드를 조회해오는지 확인")
  public void find_Dashboards_By_KeyType() {
    for (KeyType keyType : KeyType.values()) {
      List<Dashboard> page = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(
          Period.DAILY,
          "asc", null, null, 11, keyType);
      assertThat(page).allMatch(dashboard -> dashboard.getKeyType() == keyType);
    }
  }

  @Test
  @DisplayName("Period 의 조건에 따라 대시보드를 조회해오는지 확인")
  public void find_Dashboards_By_Period() {
    for (Period period : Period.values()) {
      List<Dashboard> page = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(period,
          "asc", null, null, 11, KeyType.BOOK);
      assertThat(page).allMatch(dashboard -> dashboard.getPeriod() == period);
    }
  }

  @Test
  @DisplayName("페이지네이션 동작 여부 확인")
  public void test_Pagination_Limit() {

    // 첫 번째 페이지
    List<Dashboard> firstPage = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(Period.DAILY,
        "asc", null, null, 11, KeyType.USER);

    assertEquals(11, firstPage.size());
    for (int i = 0; i < firstPage.size() - 1; i++) {
      assertTrue(firstPage.get(i).getRank() < firstPage.get(i + 1).getRank(),
          "랭킹 오름차순 정렬");
    }

    // 두 번째 페이지
    String cursor = String.valueOf(firstPage.get(firstPage.size() - 1).getRank());
    String after = String.valueOf(firstPage.get(firstPage.size() - 1).getCreatedAt());
    List<Dashboard> secondPage = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(Period.DAILY,
        "asc", cursor, after, 11, KeyType.USER);

    // cursor 이후의 데이터가 반환되는지 확인
    assertTrue(secondPage.get(0).getRank() >= firstPage.get(firstPage.size() - 1).getRank(),
        "두 번째 페이지의 첫 번째 항목은 첫 페이지의 마지막 항목보다 rank가 커야 함");
  }

  @Test
  @DisplayName("동점자(rank 중복) 존재 시에도 페이지네이션 동작")
  public void test_Find_PowerUser_with_Tied_Ranks() {
    Dashboard tiedDashboard = Dashboard.builder() // 동점자 처리 테스트 용
        .key(UUID.randomUUID())
        .keyType(KeyType.USER)
        .period(Period.DAILY)
        .value(70)
        .rank(20)
        .valueType(ValueType.SCORE)
        .build();

    testEntityManager.persist(tiedDashboard);

    for (int i = 1; i <= 10; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.USER)
          .period(Period.DAILY)
          .value(70 - i)
          .rank(21 + i) // 20 등이 2명 일시, 그 다음 사람은 22 -> 20 20 22
          .valueType(ValueType.SCORE)
          .build();

      testEntityManager.persist(dashboard);

    }

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

    // 첫 페이지
    List<Dashboard> firstPage = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(
        Period.DAILY,
        "asc", null, null, 21, KeyType.USER); // 동점자 포함해서 rank=20이 2개이므로 21까지 조회

    assertEquals(21, firstPage.size(), "동점자 포함 21개 조회되어야 함");

    List<Dashboard> rank30DashBoards = firstPage.stream().filter(d -> d.getRank() == 20).toList();
    assertEquals(2, rank30DashBoards.size(), "rank=30 인 Dashboard 가 2개 있어야 함 (동점자)");
    assertTrue(
        rank30DashBoards.get(0).getCreatedAt().isBefore(rank30DashBoards.get(1).getCreatedAt()),
        "먼저 만든 rank=30이 먼저 나와야 함 (createdAt 기준)");

    // 두 번째 페이지 조회 : cursor 를 rank = 20 으로 하면 동점자 이후로 넘어가는지 확인
    String cursor = String.valueOf(20);
    String after = String.valueOf(firstPage.get(firstPage.size() - 1).getCreatedAt());
    List<Dashboard> secondPage = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(
        Period.DAILY,
        "asc", cursor, after, 10, KeyType.USER);

    assertTrue(secondPage.stream().allMatch(d -> d.getRank() > 20),
        "두 번째 페이지에는 rank=20보다 큰 값만 있어야 함");
  }

  @Test
  @DisplayName("잘못된 커서 값이 들어온 경우 기본값 처리 확인")
  public void test_Invalid_Cursor_Value() {
    List<Dashboard> result = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(
        Period.DAILY, "asc", "invalid_cursor", null, 10, KeyType.USER);
    assertEquals(result.get(0).getRank(), 1);
  }

  @Test
  @DisplayName("커서 값이 Cursor 만 전달됐을 경우에도 처리할 수 있음을 확인")
  public void test_Cursor_Without_After(){
    List<Dashboard> result = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(
        Period.DAILY, "asc", "9", null, 10, KeyType.USER);

    assertEquals(result.get(0).getRank(), 10);
  }

  @Test
  @DisplayName("오름차순 내림차순 처리 확인")
  public void test_Asc_Desc() {
    // 오름차순 테스트
    List<Dashboard> ascResult = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(
        Period.DAILY, "asc", "invalid_cursor", null, 10, KeyType.USER);

    for (int i = 0; i < ascResult.size() - 1; i++) {
      assertTrue(ascResult.get(i).getRank() < ascResult.get(i + 1).getRank());
    }

    // 내림차순 테스트
    List<Dashboard> descResult = dashboardRepositoryCustom.findDashboardsByPeriodWithCursor(
        Period.DAILY, "desc", "invalid_cursor", null, 10, KeyType.USER);

    for (int i = 0; i < ascResult.size() - 1; i++) {
      assertTrue(descResult.get(i).getRank() > ascResult.get(i + 1).getRank());
    }
  }
}