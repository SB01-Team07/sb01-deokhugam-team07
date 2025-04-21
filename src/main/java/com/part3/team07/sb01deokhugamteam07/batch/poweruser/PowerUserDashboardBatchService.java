package com.part3.team07.sb01deokhugamteam07.batch.poweruser;

import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.entity.ValueType;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PowerUserDashboardBatchService {

  private final UserRepository userRepository;

  /**
   * 파워 유저 관련 데이터를 dashboard 테이블에 저장합니다.
   **/
  public void savePowerUserDashboardData(Period period) {
    // 1. 전체 유저 조회 (is_deleted = false)

    // 2. 각 유저의 활동 정보 가져오기
    // 2-1. 해당 기간 동안 작성한 리뷰 목록 가져오기
    // 2-2. 리뷰 점수 (리뷰당 `l`ike*0.3 + comment*0.7) 합계 구하기
    // 2-3. 해당 기간 동안 좋아요한 수
    // 2-4. 해당 기간 동안 댓글 단 수
    // 3. 활동 점수 계산
    // 4. 대시보드 데이터 구성
    // 5. SCORE 기준으로 전체 유저 순위 매기기 -> 메모리에서 정렬 후 rank 지정
    // 6. 데이터베이스에 저장한다.

  }

  /**
   * 주어진 리뷰 점수, 좋아요 수, 댓글 수를 바탕으로 유저의 활동 점수를 계산하는 메서드.
   *
   * @param reviewScore  유저의 리뷰 점수
   * @param likeCount    유저가 받은 좋아요 수
   * @param commentCount 유저가 작성한 댓글 수
   * @return 계산된 활동 점수
   **/
  public double calculateScore(double reviewScore, int likeCount, int commentCount) {
    return (reviewScore * 0.5) + (likeCount * 0.2) + (commentCount * 0.3);
  }


  public List<Dashboard> assignUserRank(Map<UUID, Double> userScoreMap, Period period,
      KeyType keyType, List<Dashboard> dashboards) {

    List<Map.Entry<UUID, Double>> userScoreList = new ArrayList<>(userScoreMap.entrySet());

    // Score 기준 내림차순으로 정렬
    userScoreList.sort((user1, user2) -> user2.getValue().compareTo(user1.getValue()));

    // 순위 계산 후 대시보드에 저장
    int rank = 1;
    for (Map.Entry<UUID, Double> user : userScoreList) {
      UUID userId = user.getKey();
      double score = user.getValue();

      // 대시보드에 SCORE 가 ValueType 인 레코드에 rank 정보 추가하여 저장
      dashboards.add(
          new Dashboard(userId, keyType, period, score, ValueType.SCORE, rank)
      );
      
      // rank 증가
      rank++;
    }

    return dashboards;
  }
}
