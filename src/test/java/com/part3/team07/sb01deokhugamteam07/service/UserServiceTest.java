package com.part3.team07.sb01deokhugamteam07.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.part3.team07.sb01deokhugamteam07.dto.user.UserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserLoginRequest;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserRegisterRequest;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.exception.user.DuplicateUserEmailException;
import com.part3.team07.sb01deokhugamteam07.exception.user.IllegalUserPasswordException;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("회원가입 - 성공")
  void join() {
    UserRegisterRequest request = new UserRegisterRequest(
        "test@mail.com",
        "nickname",
        "password123"
    );

    User user = new User("nickname", "password123", "test@mail.com");

    when(userRepository.save(any(User.class)))
        .thenReturn(user);
    when(passwordEncoder.encode(any(String.class)))
        .thenReturn("encodedpassword123");

    UserDto register = userService.register(request);

    assertThat("nickname").isEqualTo(register.nickname());
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("회원가입 - 중복 이메일 실패")
  void failJoinByDuplicateEmail() throws Exception {
    UserRegisterRequest request = new UserRegisterRequest(
        "test@mail.com",
        "nickname",
        "password123"
    );

    when(userRepository.existsByEmail(request.email()))
        .thenReturn(true);

    assertThatThrownBy(() -> userService.register(request))
        .isInstanceOf(DuplicateUserEmailException.class);

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("유저 로그인 - 성공")
  void login() {
    UserLoginRequest request = new UserLoginRequest("test@mail.com", "password123");

    User user = new User("test", "encodedpassword123", "test@mail.com");

    when(userRepository.findByEmail(any(String.class)))
        .thenReturn(Optional.of(user));
    when(passwordEncoder.matches(any(String.class), any(String.class)))
        .thenReturn(true);

    UserDto loginedUser = userService.login(request);

    assertThat("test@mail.com").isEqualTo(loginedUser.email());
    assertThat("test").isEqualTo(loginedUser.nickname());

    verify(userRepository).findByEmail(any(String.class));
  }

  @Test
  @DisplayName("유저 로그인 - 실패(없는 유저)")
  void notFoundUserLogin() {
    UserLoginRequest request = new UserLoginRequest("test@mail.com", "password123");

    when(userRepository.findByEmail(any(String.class)))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.login(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("유저 로그인 - 실패(잘못된 패스워드)")
  void invalidPassword() throws Exception {
    UserLoginRequest request = new UserLoginRequest("test@mail.com", "password123");
    User user = new User("test", "realpassword", "test@mail.com");

    when(userRepository.findByEmail(any(String.class)))
        .thenReturn(Optional.of(user));

    assertThatThrownBy(() -> userService.login(request))
        .isInstanceOf(IllegalUserPasswordException.class);
  }

  @Test
  @DisplayName("유저 수정")
  void modifyUser() {
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest("newNickName");

    User oldUser = new User("old", "password1234", "old@mail.com");

    when(userRepository.existsById(any(UUID.class)))
        .thenReturn(true);
    when(userRepository.findById(any(UUID.class)))
        .thenReturn(Optional.of(oldUser));

    UserDto updatedUser = userService.update(userId, request);

    assertThat("newNickName").isEqualTo(updatedUser.nickname());

    verify(userRepository).findById(any(UUID.class));
  }

  @Test
  @DisplayName("유저 수정 - 실패(업데이트할 유저가 없음)")
  void failUserUpdate() throws Exception {
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest("newNickname");

    assertThatThrownBy(() -> userService.update(userId, request))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository, never()).findById(any(UUID.class));
  }

  @Test
  @DisplayName("사용자 정보 조회")
  void findUser() throws Exception {
    UUID userId = UUID.randomUUID();
    User user = new User("user1", "password123", "user1@mail.com");

    when(userRepository.findById(any(UUID.class)))
        .thenReturn(Optional.of(user));

    UserDto findUser = userService.find(userId);

    assertThat("user1").isEqualTo(findUser.nickname());
    assertThat("user1@mail.com").isEqualTo(findUser.email());

    verify(userRepository).findById(any(UUID.class));
  }

  @Test
  @DisplayName("사용자 정보 조회 - 실패(잘못된 id 값)")
  void failFindUserCauseId() throws Exception {
    UUID userId = null;

    assertThatThrownBy(() -> userService.find(userId))
        .isInstanceOf(IllegalArgumentException.class);

    verify(userRepository, never()).findById(any(UUID.class));
  }

  @Test
  @DisplayName("사용자 정보 조회 - 실패(찾는 유저가 없음)")
  void failFindUserCauseNotFoundUser() {
    UUID userId = UUID.randomUUID();
    User user = new User("user", "password123", "user@mail.com");

    when(userRepository.findById(any(UUID.class)))
        .thenReturn(Optional.of(user));

    UserDto findUser = userService.find(userId);

    assertThat("user").isEqualTo(findUser.nickname());
    assertThat("user@mail.com").isEqualTo(findUser.email());

    verify(userRepository).findById(any(UUID.class));
  }

  @Test
  @DisplayName("사용자 논리 삭제")
  void logicalDelete() {
    UUID userId = UUID.randomUUID();
    User user = new User("user", "password123", "user@mail.com");
    user.logiDelete();

    when(userRepository.existsById(any(UUID.class)))
        .thenReturn(true);
    when(userRepository.findById(any(UUID.class)))
        .thenReturn(Optional.of(user));

    userService.logicalDelete(userId);

    assertThatThrownBy(() -> userService.find(userId))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자 논리 삭제 - 실패(잘못된 id)")
  void failLogicalDeleteCauseInvalidId() throws Exception {
    UUID userId = null;

    assertThatThrownBy(() -> userService.logicalDelete(userId))
        .isInstanceOf(IllegalArgumentException.class);

    verify(userRepository, never()).findById(any(UUID.class));
  }

  @Test
  @DisplayName("사용자 논리 삭제 - 실패(사용자 정보 없음)")
  void failLogicalDeleteCauseNotFoundUser() throws Exception {
    UUID userId = UUID.randomUUID();

    assertThatThrownBy(() -> userService.logicalDelete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}