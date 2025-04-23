package com.part3.team07.sb01deokhugamteam07.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.config.SecurityConfig;
import com.part3.team07.sb01deokhugamteam07.dto.user.UserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserLoginRequest;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserRegisterRequest;
import com.part3.team07.sb01deokhugamteam07.exception.user.DuplicateUserEmailException;
import com.part3.team07.sb01deokhugamteam07.exception.user.IllegalUserPasswordException;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetailsService;
import com.part3.team07.sb01deokhugamteam07.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, CustomUserDetailsService.class})
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserRepository userRepository;

  @MockitoBean
  private AuthenticationManager authenticationManager;

  @Test
  @DisplayName("POST /api/users - 회원가입 성공")
  void register() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest("test@mail.com", "test", "password123");
    String requestJson = objectMapper.writeValueAsString(request);

    UUID userId = UUID.randomUUID();
    UserDto response = new UserDto(userId, "test@mail.com", "test", LocalDateTime.now());
    String responseJson = objectMapper.writeValueAsString(response);

    // when
    when(userService.register(any(UserRegisterRequest.class)))
        .thenReturn(response);

    // then
    mockMvc
        .perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(content().json(responseJson));
  }

  @Test
  @DisplayName("POST /api/users - 회원가입 실패(중복 이메일)")
  void failRegisterCauseDuplicateEmail() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest("test@mail.com", "test", "password123");
    String requestJson = objectMapper.writeValueAsString(request);

    // when
    when(userService.register(any(UserRegisterRequest.class)))
        .thenThrow(new DuplicateUserEmailException(request));

    // then
    mockMvc
        .perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("POST /api/users - 회원가입 실패(잘못된 값)")
  void failRegisterCauseInvalidValue() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest("testmail.com", "test", "11");
    String requestJson = objectMapper.writeValueAsString(request);

    // then
    mockMvc
        .perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").value("testmail.com"))
        .andExpect(jsonPath("$.nickname").value("test"))
    ;
  }

  @Test
  @DisplayName("POST /api/users/login - 로그인")
  void login() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest("test@mail.com", "password123");
    String requestJson = objectMapper.writeValueAsString(request);

    UUID userId = UUID.randomUUID();
    UserDto response = new UserDto(userId, "testmail.com", "test", LocalDateTime.now());
    String responseJson = objectMapper.writeValueAsString(response);

    // when
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(new UsernamePasswordAuthenticationToken(
            request.email(),
            null,
            List.of()
        ));
    when(userService.login(any(UserLoginRequest.class)))
        .thenReturn(response);

    // then
    mockMvc
        .perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(content().json(responseJson))
    ;
  }

  @Test
  @DisplayName("POST /api/users/login - 로그인 실퍠(존재하지 않는 유저)")
  void failLoginCauseNotFoundUser() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest("test@mail.com", "password123");
    String requestJson = objectMapper.writeValueAsString(request);

    // when
    when(userService.login(any(UserLoginRequest.class)))
        .thenThrow(new UserNotFoundException(request));

    // then
    mockMvc
        .perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isNotFound())
    ;
  }

  @Test
  @DisplayName("POST /api/users/login - 로그인 실퍠(존재하지 않는 유저)")
  void failLoginCauseInvalidPassword() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest("test@mail.com", "password123");
    String requestJson = objectMapper.writeValueAsString(request);

    // when
    when(userService.login(any(UserLoginRequest.class)))
        .thenThrow(new IllegalUserPasswordException(request));

    mockMvc
        .perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.email").value("test@mail.com"))
    ;
  }
}