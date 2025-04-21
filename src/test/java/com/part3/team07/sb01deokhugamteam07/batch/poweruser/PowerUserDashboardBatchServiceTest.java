package com.part3.team07.sb01deokhugamteam07.batch.poweruser;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PowerUserDashboardBatchServiceTest {

  @InjectMocks
  private PowerUserDashboardBatchService powerUserDashboardBatchService;

  @Test
  @DisplayName("rank 의 기준이 되는 score 계산 로직")
  void UserScoreCalculation() {
    double reviewScore = 80;
    int likeCount = 10;
    int commentCount = 5;

    double score = powerUserDashboardBatchService.calculateScore(reviewScore, likeCount,
        commentCount);
    assertThat(score).isEqualTo(43.5);
  }


}