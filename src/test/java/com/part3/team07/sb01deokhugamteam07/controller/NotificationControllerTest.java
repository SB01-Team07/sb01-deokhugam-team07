package com.part3.team07.sb01deokhugamteam07.controller;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.part3.team07.sb01deokhugamteam07.dto.notification.NotificationDto;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.response.CursorPageResponseNotificationDto;
import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetailsService;
import com.part3.team07.sb01deokhugamteam07.service.NotificationService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private NotificationService notificationService;

  @MockitoBean
  private CustomUserDetailsService customUserDetailsService; //시큐리티

  @Test
  @DisplayName("알림 조회 성공")
  void find_Notification_Success() throws Exception {
    UUID userId = UUID.randomUUID();
    String direction = "desc";
    String cursor = "2025-04-25T00:00:00";
    String after = "2025-04-25T00:00:00";
    int limit = 20;

    CursorPageResponseNotificationDto response = CursorPageResponseNotificationDto.builder()
        .hasNext(false)
        .build();

    when(notificationService.find(userId, direction, cursor, after, limit)).thenReturn(response);

    mockMvc.perform(get("/api/notifications")
            .param("userId", userId.toString())
            .param("direction", direction)
            .param("cursor", cursor)
            .param("after", after)
            .param("limit", String.valueOf(limit))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hasNext").value(false));
  }

  @Test
  @DisplayName("잘못된 요청 - direction 값 오류 시 400 반환")
  void find_Notification_IllegalArgument_Fail() throws Exception {
    UUID userId = UUID.randomUUID();
    String direction = "IllegalArgument"; // 잘못된 값
    String cursor = "2025-04-25T00:00:00";
    String after = "2025-04-25T00:00:00";
    int limit = 20;

    mockMvc.perform(get("/api/notifications")
            .param("userId", userId.toString())
            .param("direction", direction)
            .param("cursor", cursor)
            .param("after", after)
            .param("limit", String.valueOf(limit))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("알림 업데이트 성공")
  void update_Notification_Success() throws Exception {
    UUID notificationId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID reviewId = UUID.randomUUID();
    String requestHeader = "Deokhugam-Request-User-ID";

    NotificationUpdateRequest notificationUpdateRequest = new NotificationUpdateRequest(true);
    NotificationDto notificationDto = NotificationDto.builder()
        .id(notificationId)
        .userId(userId)
        .reviewId(reviewId)
        .reviewTitle("test review title - wow good book")
        .content("나의 리뷰가 역대 인기 리뷰 9위에 선정되었습니다.")
        .confirmed(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    when(notificationService.update(any(UUID.class), any(UUID.class),
        any(NotificationUpdateRequest.class))).thenReturn(notificationDto);

    mockMvc.perform(patch("/api/notifications/{notificationId}", notificationId)
        .header(requestHeader, userId.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(notificationUpdateRequest))
        .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(notificationId.toString()))
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.confirmed").value(true));
  }

  @Test
  @DisplayName("잘못된 요청 - 요청자의 ID 누락")
  void update_Notification_IllegalArgument_Fail() throws Exception {
    UUID notificationId = UUID.randomUUID();

    NotificationUpdateRequest notificationUpdateRequest = new NotificationUpdateRequest(true);

    mockMvc.perform(patch("/api/notifications/{notificationId}", notificationId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(notificationUpdateRequest))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }
}