package com.part3.team07.sb01deokhugamteam07.batch.poweruser;

import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DateRangeUtil {
  private final Clock clock;

  public LocalDate[] getDateRange(Period period) {
    LocalDate now = LocalDate.now(clock);
    return switch (period){
      case DAILY -> new LocalDate[] {now, now};
      case WEEKLY -> new LocalDate[] {now.with(DayOfWeek.MONDAY), now.with(DayOfWeek.SUNDAY)};
      case MONTHLY -> new LocalDate[] {now.withDayOfMonth(1), now.withDayOfMonth(now.lengthOfMonth())};
      case ALL_TIME -> new LocalDate[] { LocalDate.of(1999, 1, 1), now};
    };
  }
}
