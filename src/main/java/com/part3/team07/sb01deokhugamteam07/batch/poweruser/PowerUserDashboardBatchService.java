package com.part3.team07.sb01deokhugamteam07.batch.poweruser;

import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PowerUserDashboardBatchService {

  private final UserRepository userRepository;

  /**
   * 파워 유저 관련 데이터를 dashboard 테이블에 저장합니다.
   **/
  public void savePowerUserDashboardData() {
    // 1. 전체 유저 조회 (is_deleted = false)

    // 2. 각 유저의 활동 정보 가져오기
    // 2-1. 해당 기간 동안 작성한 리뷰 목록 가져오기
    // 2-2. 리뷰 점수 (리뷰당 `l`ike*0.3 + comment*0.7) 합계 구하기
    // 2-3. 해당 기간 동안 좋아요한 수
    // 2-4. 해당 기간 동안 댓글 단 수
    // 3. 활동 점수 계산
    // 4. 대시보드 데이터 구성

    // 5. SCORE 기준으로 전체 유저 순위 매기기 -> 메모리에서 정렬 후 rank 지정
  }

  public double calculateScore(double reviewScore, int likeCount, int commentCount) {
    return (reviewScore * 0.5) + (likeCount * 0.2) + (commentCount * 0.3);
  }
}
