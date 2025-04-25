package com.part3.team07.sb01deokhugamteam07.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.part3.team07.sb01deokhugamteam07.dto.notification.NotificationDto;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationType;
import com.part3.team07.sb01deokhugamteam07.dto.notification.response.CursorPageResponseNotificationDto;
import com.part3.team07.sb01deokhugamteam07.entity.Notification;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.repository.NotificationRepository;
import com.part3.team07.sb01deokhugamteam07.repository.NotificationRepositoryCustom;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private NotificationRepositoryCustom notificationRepositoryCustom;

  @InjectMocks
  private NotificationService notificationService;

  private UUID senderId;
  private User sender;
  private UUID receiverId;
  private User receiver;
  private UUID reviewId;
  private Review review;

  @BeforeEach
  void setup() {
    senderId = UUID.randomUUID();
    sender = User.builder().nickname("testUser").build();
    ReflectionTestUtils.setField(sender, "id", senderId);

    receiverId = UUID.randomUUID();
    receiver = User.builder().build();
    ReflectionTestUtils.setField(receiver, "id", receiverId);

    reviewId = UUID.randomUUID();
    review = Review.builder().user(receiver).build();
  }

  @Test
  @DisplayName("알림 생성 성공 : 좋아요")
  void create_Success_When_Review_is_Liked() {
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

  @Test
  @DisplayName("알림 생성 성공 : 댓글")
  void create_Success_When_Review_is_Commented() {
    NotificationCreateRequest request = NotificationCreateRequest.builder()
        .type(NotificationType.REVIEW_COMMENTED)
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

  @Test
  @DisplayName("알림 생성 성공 : 순위")
  void create_Success_When_Review_is_Ranked() {
    NotificationCreateRequest request = NotificationCreateRequest.builder()
        .type(NotificationType.REVIEW_COMMENTED)
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

  @Test
  @DisplayName("알림 생성 실패 : 오류 발생시 이외의 프로레스에 예외가 전파되지 않게 한다")
  void create_Fail_When_Review_Is_Liked() {

    NotificationCreateRequest request = NotificationCreateRequest.builder()
        .type(NotificationType.REVIEW_LIKED)
        .senderId(senderId)
        .reviewId(reviewId)
        .build();

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(sender));
    when(reviewRepository.findById(any(UUID.class))).thenReturn(Optional.of(review));
    when(notificationRepository.save(any(Notification.class))).thenThrow(
        new DataAccessResourceFailureException("DB 연결 실패"));

    assertDoesNotThrow(() -> notificationService.create(request));
  }

  @Test
  @DisplayName("알림 목록 조회 성공")
  void find_Success() {
    // given
    UUID userId = UUID.randomUUID();
    String direction = "desc";
    LocalDateTime cursor = LocalDateTime.now();
    int limit = 20;

    Notification notification = Notification.builder()
        .userId(userId)
        .build();
    ReflectionTestUtils.setField(notification, "createdAt", cursor);
    List<Notification> notifications = List.of(notification);

    when(notificationRepositoryCustom.findByUserIdWithCursor(userId, direction, null, null,
        limit + 1)).thenReturn(notifications);

    // when
    CursorPageResponseNotificationDto result = notificationService.find(
        userId,
        direction,
        null,
        null,
        limit
    );

    // then
    assertThat(result).isNotNull();
    assertThat(result.nextCursor()).isEqualTo(null);
    assertFalse(result.hasNext());
    assertThat(result.content().get(0).userId()).isEqualTo(userId);
  }
}