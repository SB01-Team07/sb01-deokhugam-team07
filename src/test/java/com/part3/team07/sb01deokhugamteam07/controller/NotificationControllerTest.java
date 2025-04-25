package com.part3.team07.sb01deokhugamteam07.controller;


import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.part3.team07.sb01deokhugamteam07.dto.notification.response.CursorPageResponseNotificationDto;
import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetailsService;
import com.part3.team07.sb01deokhugamteam07.service.NotificationService;
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
  void find_Notification_IllegalArgument_Fail() throws Exception{
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
}