package com.part3.team07.sb01deokhugamteam07.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.part3.team07.sb01deokhugamteam07.dto.user.PowerUserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.UserMetricsDTO;
import com.part3.team07.sb01deokhugamteam07.dto.user.response.CursorPageResponsePowerUserDto;
import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.entity.ValueType;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepository;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepositoryCustom;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

  @Mock
  private DashboardRepositoryCustom dashboardRepositoryCustom;

  @Mock
  private DashboardRepository dashboardRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DashboardService dashboardService;

  @Test
  @DisplayName("주간 파워 유저를 정상적으로 조회")
  void getPowerUserWeeklySuccess() {
    // 기본 조회 확인
    // given
    int limit = 10;
    Period period = Period.WEEKLY;
    UUID userId = UUID.randomUUID();

    // dashboardRepositoryCustom 반환 목 객체
    List<Dashboard> mockDashboards = List.of(
        new Dashboard(userId, KeyType.USER, period, 78.69999999999999,
            ValueType.SCORE, 1)
    );
    // userRepository 반환 목 객체
    User user = User.builder()
        .nickname("testUser")
        .password("password")
        .email("test@domain.com")
        .build();
    // private 필드 'id' 를 강제로 세팅
    ReflectionTestUtils.setField(user, "id", userId);
    List<User> mockUsers = List.of(
        user
    );

    // dashboardRepository 반환 목 객체
    List<UserMetricsDTO> mockUserMetrics = List.of(new UserMetricsDTO(userId,
        94.19999999999999,
        38.0,
        80.0));
    // content 으로 쓰이는 PowerUserDto 객체
    List<PowerUserDto> mockPowerUsers = List.of(
        new PowerUserDto(
            UUID.randomUUID(),
            "testUser1",
            period,
            LocalDateTime.now(),
            1,
            78.69999999999999,
            94.19999999999999,
            38,
            80
        )
    );

    // dashboardService 반환
    CursorPageResponsePowerUserDto cursorPageResponsePowerUserDto = new CursorPageResponsePowerUserDto(
        mockPowerUsers,
        null,
        null,
        10,
        1,
        false
    );

    when(dashboardRepositoryCustom.findPowerUsersByPeriod(
        eq(period), eq("ASC"), eq(null), eq(null), eq(limit + 1))
    ).thenReturn(mockDashboards);

    when(userRepository.findAllById(any(List.class))).thenReturn(mockUsers);

    when(dashboardRepository.getUserMetrics(eq(period))).thenReturn(mockUserMetrics);

    when(dashboardRepository.countByKeyTypeAndPeriod(eq(KeyType.USER),
        eq(Period.WEEKLY))).thenReturn(1L);

    // when
    CursorPageResponsePowerUserDto result = dashboardService.getPowerUsers(
        period, // 랭킹 기간
        "ASC", // direction
        null, // cursor
        null, // atter
        limit // limit
    );

    // then
    assertThat(result).isNotNull();
    assertThat(result.content().get(0).rank()).isEqualTo(1);
  }
}