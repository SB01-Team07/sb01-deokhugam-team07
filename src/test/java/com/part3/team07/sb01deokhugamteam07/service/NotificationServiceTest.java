package com.part3.team07.sb01deokhugamteam07.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.part3.team07.sb01deokhugamteam07.dto.notification.NotificationDto;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationType;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.response.CursorPageResponseNotificationDto;
import com.part3.team07.sb01deokhugamteam07.entity.Notification;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.exception.notification.NotificationNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
import com.part3.team07.sb01deokhugamteam07.repository.NotificationRepository;
import com.part3.team07.sb01deokhugamteam07.repository.NotificationRepositoryCustom;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.swing.text.html.Option;
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

  /**
   * 알림 생성 관련 테스트
   **/
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

    when(userRepository.existsById(any(UUID.class))).thenReturn(true);
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

  @Test
  @DisplayName("알림 목록 조회 성공 - 다음 페이지가 있는 경우")
  void find_Success_With_NextPage() {
    // given
    UUID userId = UUID.randomUUID();
    String direction = "desc";
    LocalDateTime cursor = LocalDateTime.now();
    int limit = 20;

    List<Notification> notifications = new ArrayList<>();
    for (int i = 0; i <= limit; i++) {
      Notification notification = Notification.builder()
          .userId(userId)
          .content("Test content")
          .build();
      ReflectionTestUtils.setField(notification, "createdAt", cursor.minusMinutes(i));
      notifications.add(notification);
    }

    when(userRepository.existsById(any(UUID.class))).thenReturn(true);
    when(notificationRepositoryCustom.findByUserIdWithCursor(userId, direction, null, null,
        limit + 1)).thenReturn(notifications);
    when(notificationRepository.countAllByUserId(userId)).thenReturn((long) notifications.size());

    // when
    CursorPageResponseNotificationDto result = notificationService.find(
        userId,
        direction,
        null,
        null,
        limit
    );

    assertThat(result).isNotNull();
    assertThat(result.nextCursor()).isNotNull();
    assertTrue(result.hasNext());
    assertThat(result.content().size()).isEqualTo(limit);
  }

  @Test
  @DisplayName("userId가 존재하지 않는 userId 의 경우 UserNotFound 에러를 반환한다.")
  public void find_Fail_With_NullUserId() {
    // given
    UUID nonUserId = UUID.randomUUID();

    when(userRepository.existsById(nonUserId)).thenReturn(false);

    assertThrows(UserNotFoundException.class, () -> {
      notificationService.find(nonUserId, "desc", null, null, 20);
    });
  }

  /**
   * 알림 읽음 상태 처리 관련 테스트
   * **/
  @Test
  @DisplayName("알림 업데이트 성공")
  public void update_Success(){
    UUID notificationId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    NotificationUpdateRequest request = new NotificationUpdateRequest(true);

    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt =  LocalDateTime.now();
    Notification notification = Notification.builder()
        .userId(userId)
        .reviewId(reviewId)
        .content("나의 리뷰가 역대 인기 리뷰 9위에 선정되었습니다.")
        .confirmed(false)
        .build();
    ReflectionTestUtils.setField(notification, "id", notificationId);
    ReflectionTestUtils.setField(notification, "createdAt", createdAt);
    ReflectionTestUtils.setField(notification, "updatedAt", updatedAt);

    when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
    when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

    NotificationDto result = notificationService.update(notificationId, userId, request);

    verify(notificationRepository).findById(any(UUID.class));
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(notificationId);
    assertThat(result.userId()).isEqualTo(userId);
    assertTrue(result.confirmed());
  }


  @Test
  @DisplayName("notificationId 가 존재하지 않는 ID 일 경우 NotificationNotFoundException 를 반환한다.")
  public void update_Fail_With_NonNotificationId(){
    UUID NonNotificationId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    NotificationUpdateRequest request = new NotificationUpdateRequest(true);

    when(notificationRepository.findById(NonNotificationId)).thenReturn(Optional.empty() );

    assertThrows(NotificationNotFoundException.class, () -> {
      notificationService.update(NonNotificationId, userId, request);
    });
  }

  /**
   * 모든 알림 상태 읽음 처리 관련 테스트
   * **/
  @Test
  @DisplayName("모든 알림을 읽음 상태로 업데이트 성공")
  public void update_All_Success(){
    UUID userId = UUID.randomUUID();
    Notification notification1 = Notification.builder()
        .confirmed(false)
        .build();
    Notification notification2 = Notification.builder()
        .confirmed(false)
        .build();
    List<Notification> notifications = List.of(notification1, notification2);

    when(notificationRepository.findAllByUserId(userId)).thenReturn(notifications);
    notificationService.updateAll(userId);

    verify(notificationRepository).findAllByUserId(userId);
    verify(userRepository, never()).existsById(any());
    assertTrue(notification1.isConfirmed());
    assertTrue(notification2.isConfirmed());
  }

  @Test
  @DisplayName("빈 리스트가 반환된 경우 user 의 유무를 확인하고, 없을시 UserNotFoundException 를 반환한다.")
  public void update_All_Fail_With_NonNotificationId(){
    UUID userId = UUID.randomUUID();

    when(notificationRepository.findAllByUserId(userId)).thenReturn(List.of());

    assertThrows(UserNotFoundException.class, () -> {
      notificationService.updateAll(userId);
    });

    verify(notificationRepository).findAllByUserId(userId);
    verify(userRepository).existsById(userId);
  }

  /**
   * 알림 삭제 관련 테스트
   * **/
  @Test
  @DisplayName("알림 삭제 성공")
  public void delete_Success(){
    notificationService.delete();

    verify(notificationRepository).deleteAllByConfirmedTrueAndCreatedAtBefore(any(LocalDateTime.class));
  }
}