package com.part3.team07.sb01deokhugamteam07.service;


import com.part3.team07.sb01deokhugamteam07.dto.notification.NotificationDto;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.response.CursorPageResponseNotificationDto;
import com.part3.team07.sb01deokhugamteam07.entity.Notification;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
import com.part3.team07.sb01deokhugamteam07.repository.NotificationRepository;
import com.part3.team07.sb01deokhugamteam07.repository.NotificationRepositoryCustom;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationRepositoryCustom notificationRepositoryCustom;
  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;

  /**
   * 알림을 생성합니다.
   *
   * @param request 알림 생성 관련 정보를 담은 요청 객체
   **/
  public void create(NotificationCreateRequest request) {
    try {
      log.info("알림 생성 시작");
      User sender = userRepository.findById(request.getSenderId())
          .orElseThrow(() -> new UserNotFoundException(request.getSenderId()));

      Review review = reviewRepository.findById(request.getReviewId())
          .orElseThrow(() -> new NoSuchElementException(String.valueOf(request.getReviewId())));

      User receiver = review.getUser();

      String content = switch (request.getType()) {
        case REVIEW_LIKED -> "[" + sender.getNickname() + "]" + "님이 나의 리뷰를 좋아합니다.";
        case REVIEW_COMMENTED -> "[" + sender.getNickname() + "]" + "님이 나의 리뷰에 댓글을 남겼습니다.";
        case REVIEW_RANKED ->
            "나의 리뷰가" + request.getPeriod() + " 인기 리뷰" + request.getRank() + "위에 선정되었습니다.";
      };

      Notification notification = Notification.builder()
          .userId(receiver.getId())
          .reviewId(request.getReviewId())
          .content(content)
          .confirmed(false)
          .build();

      notificationRepository.save(notification);
      log.info("알림 생성 완료");
    } catch (Exception e) {
      log.warn("알림 생성 실패: {}", e.getMessage(), e);
    }
  }

  /**
   * 알림 목록을 커서 기반 페이지네이션으로 조회합니다.
   *
   * @param userId    알림을 조회할 사용자 ID
   * @param direction 정렬 방향 (asc 또는 desc, 기본값은 desc)
   * @param cursor    특정 시간 이후의 알림만 조회 (동일)
   * @param after     특정 시간 이후의 알림만 조회 (동일)
   * @param limit     한 페이지에 조회할 알림 개수 (기본값 20)
   * @return CursorPageResponseNotificationDto 알림 목록 응답
   **/
  public CursorPageResponseNotificationDto find(UUID userId, String direction, String cursor,
      String after, int limit) {
    log.info(
        "NotificationService.find() 호출 : userId={}, direction={}, cursor={}, after={}, limit={}",
        userId, direction, cursor, after, limit);

    // 사용자 정보 없음
    if(!userRepository.existsById(userId)){
      throw new UserNotFoundException(userId);
    }

    // 1. 커스텀 레포지토리에서 조회
    List<Notification> notifications = notificationRepositoryCustom.findByUserIdWithCursor(
        userId,
        direction,
        cursor,
        after,
        limit + 1 // + 1 다음 페이지 존재 여부 확인
    );

    // 2. 다음 페이지 존재 여부 판단 및 실제 리스트 잘라내기
    boolean hasNext = notifications.size() > limit;
    if (hasNext) {
      notifications = notifications.subList(0, limit);
    }

    // 3. NotificationDto 변환
    List<NotificationDto> content = new ArrayList<>();
    for (Notification n : notifications) {
      content.add(
          NotificationDto.builder()
              .id(n.getId())
              .userId(n.getUserId())
              .reviewId(n.getReviewId())
              .reviewTitle(n.getContent())
              .content(n.getContent())
              .confirmed(n.isConfirmed())
              .createdAt(n.getCreatedAt())
              .updatedAt(n.getUpdatedAt())
              .build()
      );
    }

    // 4. 다음 페이지 커서 및 after 값 설정 (동일)
    String nextAfter =
        hasNext ? String.valueOf(notifications.get(notifications.size() - 1).getCreatedAt()) : null;

    // 5. 전체 알림 수
    long totalElement = notificationRepository.countAllByUserId(userId);

    log.info("Notification 조회 완료: 총 {}개 중 {}개 반환, 다음 페이지: {}", totalElement, content.size(),
        hasNext);
    return CursorPageResponseNotificationDto.builder()
        .content(content)
        .nextCursor(nextAfter)
        .nextAfter(nextAfter)
        .size(content.size())
        .hasNext(hasNext)
        .build();
  }

  public NotificationDto update(UUID notificationId, UUID userId, NotificationUpdateRequest request) {
    return NotificationDto.builder().build();
  }
}
