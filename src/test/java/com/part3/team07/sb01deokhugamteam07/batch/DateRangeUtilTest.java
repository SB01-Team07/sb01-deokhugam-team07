package com.part3.team07.sb01deokhugamteam07.batch;

import static org.junit.jupiter.api.Assertions.*;

import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DateRangeUtilTest {

  @Test
  @DisplayName("WEEKLY 기준 날짜 범위가 정확히 계산되는지 확인")
  void getDateRage_shouldReturnCorrectRange_forWeekly() {
    // 2025 년 4월 16일 00:00 (한국 시간) < 수요일
    Clock fixedClock = Clock.fixed(
        LocalDate.of(2025, 4, 16) // 날짜 객체
            .atStartOfDay(ZoneId.systemDefault()) // 하루 시작 시간과 시간대 설정
            .toInstant(), // UTC Instant 변환
        ZoneId.systemDefault() // 고정 시게의 시간대
    );

    DateRangeUtil util = new DateRangeUtil(fixedClock);

    LocalDate[] range = util.getDateRange(Period.WEEKLY);

    assertEquals(LocalDate.of(2025, 4, 14), range[0]); // 월요일
    assertEquals(LocalDate.of(2025, 4, 20), range[1]); // 일요일
  }

  @Test
  @DisplayName("MONTHLY  기준 날짜 범위가 정확히 계산되는지 확인")
  void getDateRage_shouldReturnCorrectRange_forMonthly() {
    // 2025 년 4월 16일 00:00 (한국 시간) < 수요일
    Clock fixedClock = Clock.fixed(
        LocalDate.of(2025, 4, 16) // 날짜 객체
            .atStartOfDay(ZoneId.systemDefault()) // 하루 시작 시간과 시간대 설정
            .toInstant(), // UTC Instant 변환
        ZoneId.systemDefault() // 고정 시게의 시간대
    );

    DateRangeUtil util = new DateRangeUtil(fixedClock);

    LocalDate[] range = util.getDateRange(Period.MONTHLY);

    assertEquals(LocalDate.of(2025, 4, 1), range[0]); // 월요일
    assertEquals(LocalDate.of(2025, 4, 30), range[1]); // 일요일
  }

  @Test
  @DisplayName("ALL_TIME 기준 날짜 범위가 정확히 계산되는지 확인")
  void getDateRage_shouldReturnCorrectRange_AllTime() {
    // 2025 년 4월 16일 00:00 (한국 시간) < 수요일
    Clock fixedClock = Clock.fixed(
        LocalDate.of(2025, 4, 16) // 날짜 객체
            .atStartOfDay(ZoneId.systemDefault()) // 하루 시작 시간과 시간대 설정
            .toInstant(), // UTC Instant 변환
        ZoneId.systemDefault() // 고정 시게의 시간대
    );

    DateRangeUtil util = new DateRangeUtil(fixedClock);

    LocalDate[] range = util.getDateRange(Period.ALL_TIME);

    assertEquals(LocalDate.of(1999, 1, 1), range[0]); // 월요일
    assertEquals(LocalDate.of(2025, 4, 16), range[1]); // 일요일
  }
}