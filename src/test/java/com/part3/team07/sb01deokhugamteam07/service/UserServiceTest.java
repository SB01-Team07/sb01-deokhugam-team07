package com.part3.team07.sb01deokhugamteam07.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.part3.team07.sb01deokhugamteam07.dto.user.UserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserRegisterRequest;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.exception.user.DuplicateUserEmailException;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
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
}