package com.part3.team07.sb01deokhugamteam07.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.user.UserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserLoginRequest;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserRegisterRequest;
import com.part3.team07.sb01deokhugamteam07.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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
    UserRegisterRequest regReq = new UserRegisterRequest("test33@mail.com", "user", "password1234");
    UserDto registered = userService.register(regReq);
    UserLoginRequest request = new UserLoginRequest("test33@mail.com", "password1234");

    String requestJson = objectMapper.writeValueAsString(request);
    String userJson = objectMapper.writeValueAsString(registered);

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(content().json(userJson))
        .andExpect(header().string("Deokhugam-Request-User-ID", registered.id().toString()));

  }
}
