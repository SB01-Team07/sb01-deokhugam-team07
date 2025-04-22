package com.part3.team07.sb01deokhugamteam07.batch.poweruser;

import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.ValueType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AssignRankUtil {

  public List<Dashboard> assignRank(Map<UUID, Double> scoreMap, Period period,
      KeyType keyType, List<Dashboard> dashboards) {

    List<Map.Entry<UUID, Double>> scoreList = new ArrayList<>(scoreMap.entrySet());

    // Score 기준 내림차순으로 정렬
    scoreList.sort((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));

    // 정렬된 점수를 기반으로 순위 할당
    int rank = 1;
    double prevScore = 0.0;
    for (int i = 0; i < scoreList.size(); i++) {
      Map.Entry<UUID, Double> entry = scoreList.get(i);
      UUID id = entry.getKey();
      double score = entry.getValue();

      if (Double.compare(score, prevScore) != 0) {
        rank = i + 1; // 새로운 점수일시 rank 증가, 동점자 처리
      }

      // ValueType : SCORE 인 Dashboard 객체에 rank 정보 추가하여 저장
      dashboards.add(
          new Dashboard(id, keyType, period, score, ValueType.SCORE, rank)
      );

      prevScore = score;
    }

    return dashboards;
  }
}

