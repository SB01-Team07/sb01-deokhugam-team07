package com.part3.team07.sb01deokhugamteam07.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.user.UserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserLoginRequest;
import com.part3.team07.sb01deokhugamteam07.service.UserService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService;

  @Test
  @DisplayName("POST /api/users/login - Deokhugam-Request-User-ID 응답 헤더 확인")
  void checkUserLoginWithResponseHeader() throws Exception {
    UserLoginRequest request = new UserLoginRequest("test@mail.com", "password1234");

    UUID userId = UUID.randomUUID();
    UserDto user = UserDto.builder()
        .id(userId)
        .email(request.email())
        .nickname("user")
        .createdAt(LocalDateTime.now())
        .build();

    when(userService.login(request))
        .thenReturn(user);

    String requestJson = objectMapper.writeValueAsString(request);
    String userJson = objectMapper.writeValueAsString(user);

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(content().json(userJson))
        .andExpect(header().string("Deokhugam-Request-User-ID", userId.toString()))
    ;
  }
}
