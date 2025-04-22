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

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();
  }

  /**
   * findPowerUsersByPeriod 메서드
   * 파워 유저 데이터를 담고 있는 대시보드 테이블을 조회해 페이지네이션을 지원하여 반환하는 메서드에 대한 테스트입니다.
   *
   * limit 이 11 인 이유는 서비스 단에서 hasNext 를 위해 limit + 1 값을 전달하기 때문입니다.
   **/

  @Test
  @DisplayName("Daily 기간에 대해 데이터 조회")
  public void testfindPowerUsersByDailyWithCursor() {

    for (int i = 1; i <= 40; i++) {
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

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

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
    assertTrue(secondPage.get(0).getRank() >= firstPage.get(firstPage.size() - 1).getRank(),
        "두 번째 페이지의 첫 번째 항목은 첫 페이지의 마지막 항목보다 rank가 커야 함");
  }

  @Test
  @DisplayName("Weekly 기간에 대해 데이터 조회")
  public void testfindPowerUsersByWeekly() {

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

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

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
    assertTrue(secondPage.get(0).getRank() >= firstPage.get(firstPage.size() - 1).getRank(),
        "두 번째 페이지의 첫 번째 항목은 첫 페이지의 마지막 항목보다 rank가 커야 함");

  }

  @Test
  @DisplayName("Monthly 기간에 대해 데이터 조회")
  public void testfindPowerUsersByMonthly() {

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

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

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

  @Test
  @DisplayName("동점자(rank 중복) 존재 시에도 페이지네이션 동작")
  public void test_Find_PowerUser_with_Tied_Ranks() {

    for (int i = 1; i <= 40; i++) {
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
    Dashboard tiedDashboard = Dashboard.builder() // 동점자 처리 테스트 용
        .key(UUID.randomUUID())
        .keyType(KeyType.USER)
        .period(Period.DAILY)
        .value(60)
        .rank(30)
        .valueType(ValueType.SCORE)
        .build();
    testEntityManager.persist(tiedDashboard);

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

    // 첫 페이지
    List<Dashboard> firstPage = dashboardRepositoryCustom.findPowerUsersByPeriod(Period.DAILY,
        "asc", null, null, 31); // 동점자 포함해서 rank=30이 2개이므로 31까지 조회

    assertEquals(31, firstPage.size(), "동점자 포함 31개 조회되어야 함");

    List<Dashboard> rank30DashBoards = firstPage.stream().filter(d -> d.getRank() == 30).toList();
    assertEquals(2, rank30DashBoards.size(), "rank=30 인 Dashboard 가 2개 있어야 함 (동점자)");
    assertTrue(
        rank30DashBoards.get(0).getCreatedAt().isBefore(rank30DashBoards.get(1).getCreatedAt()),
        "먼저 만든 rank=30이 먼저 나와야 함 (createdAt 기준)");

    // 두 번째 페이지 조회 : cursor 를 rank = 30 으로 하면 동점자 이후로 넘어가는지 확ㅇ니
    String cursor = String.valueOf(30);
    List<Dashboard> secondPage = dashboardRepositoryCustom.findPowerUsersByPeriod(Period.DAILY,
        "asc", cursor, null, 10);

    assertTrue(secondPage.stream().allMatch(d -> d.getRank() > 30),
        "두 번째 페이지에는 rank=30보다 큰 값만 있어야 함");
  }

  @Test
  @DisplayName("커서와 after 파라미터가 올바르게 동작")
  public void testCursorAndAfterParameters() {


    List<Dashboard> firstPage = dashboardRepositoryCustom.findPowerUsersByPeriod(
        Period.DAILY, "asc", null, null, 30);

    // 중복되는 랭크를 가져온다
    Dashboard duplicateRank = firstPage.stream()
        .filter(d -> d.getRank() == 30)
        .findFirst()
        .orElseThrow();

    String cursor = String.valueOf(duplicateRank.getRank());
    String after = duplicateRank.getCreatedAt().toString();

    List<Dashboard> secondPage = dashboardRepositoryCustom.findPowerUsersByPeriod(
        Period.DAILY, "asc", cursor, after, 30);

    assertTrue(secondPage.stream().anyMatch(d ->
        d.getRank() > duplicateRank.getRank() ||
            (d.getRank() == duplicateRank.getRank() &&
                d.getCreatedAt().isAfter(duplicateRank.getCreatedAt()))
    ));
  }

  /**
   * findPopularReviewByPeriod 메서드
   * 인기 리뷰 데이터를 담고 있는 대시보드 테이블을 조회해 페이지네이션을 지원하여 반환하는 메서드에 대한 테스트입니다.
   *
   * limit 이 51 인 이유는 서비스 단에서 hasNext 를 위해 limit + 1 값을 전달하고, limit 의 default가 50이기 때문입니다.
   **/

  @Test
  @DisplayName("Daily 기간에 대해 데이터 조회")
  public void test_Find_Popular_Review_By_Daily_With_Cursor() {

    for (int i = 1; i <= 100; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.REVIEW)
          .period(Period.DAILY)
          .value(150 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();
      testEntityManager.persist(dashboard);
    }

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

    // 첫 번째 페이지
    List<Dashboard> firstPage = dashboardRepositoryCustom.findPopularReviewByPeriod(Period.DAILY,
        "asc", null, null, 51);

    assertEquals(51, firstPage.size());
    for (int i = 0; i < firstPage.size() - 1; i++) {
      assertTrue(firstPage.get(i).getRank() < firstPage.get(i + 1).getRank(),
          "랭킹 오름차순 정렬");
    }
    // 두 번째 페이지
    String cursor = String.valueOf(firstPage.get(firstPage.size() - 1).getRank());
    String after = String.valueOf(firstPage.get(firstPage.size() - 1).getCreatedAt());
    List<Dashboard> secondPage = dashboardRepositoryCustom.findPopularReviewByPeriod(Period.DAILY,
        "asc", cursor, after, 51);

    // cursor 이후의 데이터가 반환되는지 확인
    assertTrue(secondPage.get(0).getRank() >= firstPage.get(firstPage.size() - 1).getRank(),
        "두 번째 페이지의 첫 번째 항목은 첫 페이지의 마지막 항목보다 rank가 커야 함");
  }

  @Test
  @DisplayName("Weekly 기간에 대해 데이터 조회")
  public void test_Find_Popular_Review_By_Weekly_With_Cursor() {

    for (int i = 1; i <= 100; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.REVIEW)
          .period(Period.WEEKLY)
          .value(150 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();
      testEntityManager.persist(dashboard);
    }

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

    // 첫 번째 페이지
    List<Dashboard> firstPage = dashboardRepositoryCustom.findPopularReviewByPeriod(Period.WEEKLY,
        "asc", null, null, 51);

    assertEquals(51, firstPage.size());
    for (int i = 0; i < firstPage.size() - 1; i++) {
      assertTrue(firstPage.get(i).getRank() < firstPage.get(i + 1).getRank(),
          "랭킹 오름차순 정렬");
    }
    // 두 번째 페이지
    String cursor = String.valueOf(firstPage.get(firstPage.size() - 1).getRank());
    String after = String.valueOf(firstPage.get(firstPage.size() - 1).getCreatedAt());
    List<Dashboard> secondPage = dashboardRepositoryCustom.findPopularReviewByPeriod(Period.WEEKLY,
        "asc", cursor, after, 51);

    // cursor 이후의 데이터가 반환되는지 확인
    assertTrue(secondPage.get(0).getRank() >= firstPage.get(firstPage.size() - 1).getRank(),
        "두 번째 페이지의 첫 번째 항목은 첫 페이지의 마지막 항목보다 rank가 커야 함");
  }

  @Test
  @DisplayName("Monthly 기간에 대해 데이터 조회")
  public void test_Find_Popular_Review_By_Monthly_With_Cursor() {
    for (int i = 1; i <= 100; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.REVIEW)
          .period(Period.MONTHLY)
          .value(150 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();
      testEntityManager.persist(dashboard);
    }

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

    // 첫 번째 페이지
    List<Dashboard> firstPage = dashboardRepositoryCustom.findPopularReviewByPeriod(Period.MONTHLY,
        "asc", null, null, 51);

    assertEquals(51, firstPage.size());
    for (int i = 0; i < firstPage.size() - 1; i++) {
      assertTrue(firstPage.get(i).getRank() < firstPage.get(i + 1).getRank(),
          "랭킹 오름차순 정렬");
    }
    // 두 번째 페이지
    String cursor = String.valueOf(firstPage.get(firstPage.size() - 1).getRank());
    String after = String.valueOf(firstPage.get(firstPage.size() - 1).getCreatedAt());
    List<Dashboard> secondPage = dashboardRepositoryCustom.findPopularReviewByPeriod(Period.MONTHLY,
        "asc", cursor, after, 51);

    // cursor 이후의 데이터가 반환되는지 확인
    assertTrue(secondPage.get(0).getRank() >= firstPage.get(firstPage.size() - 1).getRank(),
        "두 번째 페이지의 첫 번째 항목은 첫 페이지의 마지막 항목보다 rank가 커야 함");
  }

  @Test
  @DisplayName("All Time 기간에 대해 데이터 조회")
  public void test_Find_Popular_Review_By_All_Time_With_Cursor() {
    for (int i = 1; i <= 100; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.REVIEW)
          .period(Period.ALL_TIME)
          .value(150 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();
      testEntityManager.persist(dashboard);
    }

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

    // 첫 번째 페이지
    List<Dashboard> firstPage = dashboardRepositoryCustom.findPopularReviewByPeriod(Period.ALL_TIME,
        "asc", null, null, 51);

    assertEquals(51, firstPage.size());
    for (int i = 0; i < firstPage.size() - 1; i++) {
      assertTrue(firstPage.get(i).getRank() < firstPage.get(i + 1).getRank(),
          "랭킹 오름차순 정렬");
    }
    // 두 번째 페이지
    String cursor = String.valueOf(firstPage.get(firstPage.size() - 1).getRank());
    String after = String.valueOf(firstPage.get(firstPage.size() - 1).getCreatedAt());
    List<Dashboard> secondPage = dashboardRepositoryCustom.findPopularReviewByPeriod(Period.ALL_TIME,
        "asc", cursor, after, 51);

    // cursor 이후의 데이터가 반환되는지 확인
    assertTrue(secondPage.get(0).getRank() >= firstPage.get(firstPage.size() - 1).getRank(),
        "두 번째 페이지의 첫 번째 항목은 첫 페이지의 마지막 항목보다 rank가 커야 함");
  }

  @Test
  @DisplayName("전달한 Period 대로 데이터를 조회하고 있는지 확인")
  public void should_Find_Popular_Reviews_By_Monthly_Period_With_Cursor() {
    for (int i = 1; i <= 2; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.REVIEW)
          .period(Period.WEEKLY)
          .value(100 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();
      testEntityManager.persist(dashboard);
    }

    for (int i = 1; i <= 100; i++) {
      Dashboard dashboard = Dashboard.builder()
          .key(UUID.randomUUID())
          .keyType(KeyType.REVIEW)
          .period(Period.MONTHLY)
          .value(150 - i)
          .rank(i)
          .valueType(ValueType.SCORE)
          .build();
      testEntityManager.persist(dashboard);
    }

    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

    // 첫 번째 페이지
    List<Dashboard> firstPage = dashboardRepositoryCustom.findPopularReviewByPeriod(Period.MONTHLY,
        "asc", null, null, 51);

    // 두 번째 페이지
    String cursor = String.valueOf(firstPage.get(firstPage.size() - 1).getRank());
    String after = String.valueOf(firstPage.get(firstPage.size() - 1).getCreatedAt());
    List<Dashboard> secondPage = dashboardRepositoryCustom.findPopularReviewByPeriod(Period.MONTHLY,
        "asc", cursor, after, 51);

    // Period 가 Monthly 로 전달되고 있는지 확인
    assertEquals(Period.MONTHLY, firstPage.get(0).getPeriod());
    assertEquals(Period.MONTHLY, secondPage.get(0).getPeriod());
  }


}