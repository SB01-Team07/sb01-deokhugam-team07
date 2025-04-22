package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.util.List;

public interface DashboardRepositoryCustom {

  List<Dashboard> findPowerUsersByPeriod(
      Period period,
      String direction,
      String cursor,
      String after,
      int limit
  );

  List<Dashboard> findPopularReviewByPeriod(
      Period period,
      String direction,
      String cursor,
      String after,
      int limit);
}
