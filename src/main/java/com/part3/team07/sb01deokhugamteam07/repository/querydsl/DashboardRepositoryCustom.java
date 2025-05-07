package com.part3.team07.sb01deokhugamteam07.repository.querydsl;

import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.util.List;

public interface DashboardRepositoryCustom {
  List<Dashboard> findDashboardsByPeriodWithCursor(
      Period period,
      String direction,
      String cursor,
      String after,
      int limit,
      KeyType keyType);
}
