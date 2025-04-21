package com.part3.team07.sb01deokhugamteam07.batch.poweruser;


import static org.assertj.core.api.Assertions.assertThat;

import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.ValueType;
import com.part3.team07.sb01deokhugamteam07.repository.CommentRepository;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepository;
import com.part3.team07.sb01deokhugamteam07.repository.LikeRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PowerUserDashboardBatchServiceTest {


  @InjectMocks
  private PowerUserDashboardBatchService powerUserDashboardBatchService;

  @Test
  @DisplayName("rank 의 기준이 되는 score 계산 로직")
  void UserScore_Calculation() {
    double reviewScore = 80;
    int likeCount = 10;
    int commentCount = 5;

    double score = powerUserDashboardBatchService.calculateScore(reviewScore, likeCount,
        commentCount);
    assertThat(score).isEqualTo(43.5);
  }

  @Test
  @DisplayName("score 정렬 후 rank 지정하기")
  void AssignRank_BasedOnScore() {
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    UUID userId3 = UUID.randomUUID();
    UUID userId4 = UUID.randomUUID();

    Map<UUID, Double> userScoreMap = new HashMap<>();

    userScoreMap.put(userId1, 70.0);
    userScoreMap.put(userId2, 90.0);
    userScoreMap.put(userId3, 80.0);
    userScoreMap.put(userId4, 80.0);


    Period period = Period.WEEKLY;
    KeyType keyType = KeyType.USER;

    List<Dashboard> dashboards = new ArrayList<>();

    List<Dashboard> assignUserRank = powerUserDashboardBatchService.assignUserRank(userScoreMap,
        period, keyType, dashboards);

    assertThat(assignUserRank).hasSize(4);

    ReflectionTestUtils.setField(assignUserRank.get(0), "id", userId2);
    ReflectionTestUtils.setField(assignUserRank.get(1), "id", userId3);
    ReflectionTestUtils.setField(assignUserRank.get(2), "id", userId4);
    ReflectionTestUtils.setField(assignUserRank.get(3), "id", userId1);

    // 확인 용 Map
    Map<UUID, Integer> expectedRanks = Map.of(
        userId2, 1,
        userId3, 2,
        userId4, 2,
        userId1, 4
    );

    assertThat(
        assignUserRank.stream().filter(d -> d.getId().equals(userId3)).findFirst().get().getRank()
    ).isEqualTo(
        assignUserRank.stream().filter(d-> d.getId().equals(userId4)).findFirst().get().getRank()
    );

    for (Dashboard d : assignUserRank) {
      UUID id = d.getId();
      assertThat(d.getRank()).isEqualTo(expectedRanks.get(id));
      assertThat(d.getValue()).isEqualTo(userScoreMap.get(id));
      assertThat(d.getValueType()).isEqualTo(ValueType.SCORE);
    }
  }
}