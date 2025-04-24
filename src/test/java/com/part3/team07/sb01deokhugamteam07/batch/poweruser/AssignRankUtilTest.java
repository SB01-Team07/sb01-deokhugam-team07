package com.part3.team07.sb01deokhugamteam07.batch.poweruser;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AssignRankUtilTest {

  @Test
  @DisplayName("Rank 가 Score 의 대소 관계에 따라 할당이 되는지 확인")
  void assigns_Rank_According_to_Score_Order(){
    AssignRankUtil assignRankUtil = new AssignRankUtil();

    UUID review1 = UUID.randomUUID();
    UUID review2 = UUID.randomUUID();
    UUID review3 = UUID.randomUUID();

    Map<UUID, Double> scoreMap = new LinkedHashMap<>();

    scoreMap.put(review1, 90.0);
    scoreMap.put(review2, 80.0);
    scoreMap.put(review3, 70.0);

    Period period = Period.WEEKLY;
    KeyType keyType = KeyType.REVIEW;
    List<Dashboard> dashboards = new ArrayList<>();

    dashboards = assignRankUtil.assignRank(scoreMap, period, keyType, dashboards);

    assertEquals(review1, dashboards.get(0).getKey());
    assertEquals(1, dashboards.get(0).getRank());
    assertEquals(2, dashboards.get(1).getRank());
    assertEquals(3, dashboards.get(2).getRank());
  }

  @Test
  @DisplayName("동점인 Score 가 존재할 때 동일한 Rank 가 할당된다")
  void assigns_Equal_Rank_For_Tied_Scores(){
    AssignRankUtil assignRankUtil = new AssignRankUtil();

    UUID review1 = UUID.randomUUID();
    UUID review2 = UUID.randomUUID();
    UUID review3 = UUID.randomUUID();

    UUID review4 = UUID.randomUUID();

    Map<UUID, Double> scoreMap = new LinkedHashMap<>();

    scoreMap.put(review1, 90.0);
    scoreMap.put(review2, 80.0);
    scoreMap.put(review3, 80.0);
    scoreMap.put(review4, 70.0);

    Period period = Period.WEEKLY;
    KeyType keyType = KeyType.REVIEW;
    List<Dashboard> dashboards = new ArrayList<>();

    dashboards = assignRankUtil.assignRank(scoreMap, period, keyType, dashboards);

    assertEquals(review2, dashboards.get(1).getKey());
    assertEquals(review3, dashboards.get(2).getKey());
    assertEquals(2, dashboards.get(1).getRank());
    assertEquals(2, dashboards.get(2).getRank());
    assertEquals(dashboards.get(1).getRank(), dashboards.get(2).getRank());
  }
}