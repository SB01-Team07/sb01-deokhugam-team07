package com.part3.team07.sb01deokhugamteam07.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationType;
import com.part3.team07.sb01deokhugamteam07.entity.Notification;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.repository.NotificationRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import javax.swing.text.html.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private NotificationRepository notificationRepository;

  @InjectMocks
  private NotificationService notificationService;
  
  @Test
  @DisplayName("알림 생성 성공")
  void create_Success_When_Review_Is_Liked(){
    UUID senderId = UUID.randomUUID();
    User sender = User.builder().nickname("testUser").build();
    ReflectionTestUtils.setField(sender, "id", senderId);

    UUID receiverId = UUID.randomUUID();
    User receiver = User.builder().build();
    ReflectionTestUtils.setField(receiver, "id", receiverId);

    UUID reviewId = UUID.randomUUID();
    Review review = Review.builder().user(receiver).build();

    NotificationCreateRequest request = NotificationCreateRequest.builder()
        .type(NotificationType.REVIEW_LIKED)
        .senderId(senderId)
        .reviewId(reviewId)
        .build();

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(sender));
    when(reviewRepository.findById(any(UUID.class))).thenReturn(Optional.of(review));

    // when
    notificationService.create(request);

    verify(userRepository).findById(any());
    verify(reviewRepository).findById(any());
    verify(notificationRepository).save(any(Notification.class));
  }
}